package app.osmosi.heater.scheduler;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import app.osmosi.heater.api.Api;
import app.osmosi.heater.model.Floor;
import app.osmosi.heater.model.ScheduleItem;
import app.osmosi.heater.utils.Env;
import app.osmosi.heater.utils.IntervalThread;

public class Scheduler {
  private static final String SCHEDULE_PATH = Env.CONFIG_PATH + "/schedule.csv";
  private IntervalThread intervalThread;

  // TODO: Move both variables below to the AppState
  private int todayIs;
  private Set<ScheduleItem> triggeredToday;

  private int getNowMinutes() {
    LocalTime now = LocalTime.now();
    return (now.getHour() * 60) + now.getMinute();
  }

  private static Comparator<ScheduleItem> byTotalMinutesReversed = Comparator.comparing(ScheduleItem::getTotalMinutes)
      .reversed();

  public Optional<ScheduleItem> findScheduleItem(int nowMinutes, List<ScheduleItem> schedule, String floorName) {
    List<ScheduleItem> sorted = schedule.stream()
        .filter(i -> i.getFloorName().equals(floorName))
        .sorted(byTotalMinutesReversed).toList();

    Optional<ScheduleItem> optionalItem = sorted.stream()
        .filter(i -> nowMinutes >= i.getTotalMinutes())
        .findAny();

    if (optionalItem.isPresent()) {
      return optionalItem;
    } else if (sorted.size() > 0) {
      return Optional.of(sorted.get(0));
    }

    return Optional.empty();
  }

  public void syncInitialDesiredTemp(List<ScheduleItem> schedule, int today, int nowMinutes) {
    Api.getCurrentState().getFloors().forEach(floor -> {
      Optional<ScheduleItem> item = findScheduleItem(nowMinutes, schedule, floor.getName());

      if (item.isPresent()) {
        // Need better names for those:
        if (todayIs != today) {
          todayIs = today;
          triggeredToday = new HashSet<>();
        }
        if (!triggeredToday.contains(item.get())) {
          Api.updateFloor(floor.withDesiredTemp(item.get().getDesiredTemp()));
          triggeredToday.add(item.get());
        }
      }
    });
  }

  public void updateDesiredTemp(ScheduleItem item) {
    Floor floor = Api.getCurrentState().getFloorByName(item.getFloorName());
    Api.updateFloor(floor.withDesiredTemp(item.getDesiredTemp()));
  }

  public void start() throws IOException {
    List<ScheduleItem> currentSchedule = ScheduleParser.parse(new File(SCHEDULE_PATH));
    // Starts with the correct schedule on the first time:
    syncInitialDesiredTemp(currentSchedule, LocalDateTime.now().getDayOfMonth(), getNowMinutes());

    Map<Integer, List<ScheduleItem>> scheduleMap = currentSchedule.stream()
        .collect(Collectors.groupingBy(ScheduleItem::getTotalMinutes));

    intervalThread = new IntervalThread(() -> {
      List<ScheduleItem> items = scheduleMap.get(getNowMinutes());
      if (items != null) {
        items.forEach(this::updateDesiredTemp);
      }
    }, 60000);
    new Thread(intervalThread).start();
  }

  public void stop() {
    if (intervalThread != null) {
      intervalThread.stop();
    }
  }
}
