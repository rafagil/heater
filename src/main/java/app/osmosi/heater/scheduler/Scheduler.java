package app.osmosi.heater.scheduler;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import app.osmosi.heater.api.Api;
import app.osmosi.heater.model.AppState;
import app.osmosi.heater.model.ScheduleItem;
import app.osmosi.heater.utils.IntervalThread;

public class Scheduler {
    private static final String SCHEDULE_PATH = "/projects/heater/config/schedule.csv"; // TODO: This can't be hardcoded.
    private IntervalThread intervalThread;

    private int getNowMinutes() {
        LocalTime now = LocalTime.now();
        return (now.getHour() * 60) + now.getMinute();
    }

    private static Comparator<ScheduleItem> byTotalMinutesReversed = Comparator.comparing(ScheduleItem::getTotalMinutes)
            .reversed();

    public ScheduleItem findScheduleItem(int nowMinutes, List<ScheduleItem> schedule, String floorName) {
        List<ScheduleItem> sorted = schedule.stream()
            .filter(i -> i.getFloorName().equals(floorName))
            .sorted(byTotalMinutesReversed).toList();

        Optional<ScheduleItem> optionalItem = sorted.stream()
            .filter(i -> nowMinutes >= i.getTotalMinutes())
            .findAny();

        ScheduleItem item;
        if (optionalItem.isPresent()) {
            item = optionalItem.get();
        } else {
            item = sorted.get(0);
        }

        return item;
    }

    public void start() throws IOException {
        List<ScheduleItem> currentSchedule = ScheduleParser.parse(new File(SCHEDULE_PATH));
        intervalThread = new IntervalThread(() -> {
            AppState state = Api.getCurrentState();
            ScheduleItem cimaSchedule = findScheduleItem(getNowMinutes(), currentSchedule, state.getCima().getName());
            ScheduleItem baixoSchedule = findScheduleItem(getNowMinutes(), currentSchedule, state.getBaixo().getName());
            Api.updateFloor(state.getCima().withDesiredTemp(cimaSchedule.getDesiredTemp()));
            Api.updateFloor(state.getBaixo().withDesiredTemp(baixoSchedule.getDesiredTemp()));
        }, 30000);
        new Thread(intervalThread).start();
    }

    public void stop() {
        if (intervalThread != null) {
            intervalThread.stop();
        }
    }
}

// (defn start-scheduler []
//   (reset! running true)
//   (go-loop []
//     (check-and-trigger heater-schedule on-trigger)
//     (check-and-trigger water-timers #(api/turn-on-hot-water (:timeout %)))
//     (<! (timeout 30000))
//     (when @running (recur))))

// (defn stop-scheduler [] (reset! running false))
