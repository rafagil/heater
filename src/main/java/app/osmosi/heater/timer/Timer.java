package app.osmosi.heater.timer;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;

import app.osmosi.heater.api.Api;
import app.osmosi.heater.model.AppState;
import app.osmosi.heater.model.HotWaterTimer;
import app.osmosi.heater.utils.Env;
import app.osmosi.heater.utils.IntervalThread;

public class Timer {
  private static final String FILE_PATH = Env.CONFIG_PATH + "/hw-timers.csv";
  private IntervalThread intervalThread;

  private int getNowMinutes() {
    LocalTime now = LocalTime.now();
    return (now.getHour() * 60) + now.getMinute();
  }

  private int today() {
    return LocalDateTime.now().getDayOfMonth();
  }

  private static Comparator<HotWaterTimer> byTotalMinutesReversed = Comparator.comparing(HotWaterTimer::getTotalMinutes)
      .reversed();

  public Optional<HotWaterTimer> findTimer(int nowMinutes, Set<HotWaterTimer> timers) {
    return timers.stream()
        .sorted(byTotalMinutesReversed)
        .filter(t -> nowMinutes >= t.getTotalMinutes() && t.getDayTriggered() != today())
        .findAny();
  }

  public void reloadTimers() throws IOException {
    Set<HotWaterTimer> fileTimers = HotWaterTimerParser.parse(new File(FILE_PATH));
    Api.updateTimers(fileTimers);
  }

  public void updateHotWaterState(int nowMinutes, int today) {
    AppState state = Api.getCurrentState();
    Optional<HotWaterTimer> timer = findTimer(nowMinutes, state.getTimers());
    if (timer.isPresent()) {
      Api.turnOnHotWater(timer.get().getTimeout());
      Api.updateTimer(timer.get().withDayTriggered(today));
    }
  }

  public void start() {
    intervalThread = new IntervalThread(() -> {
      updateHotWaterState(getNowMinutes(), today());
    }, 30000);
    new Thread(intervalThread).start();
  }

  public void stop() {
    if (intervalThread != null) {
      intervalThread.stop();
    }
  }

  public boolean isRunning() {
    if (intervalThread == null) {
      return false;
    }
    return intervalThread.isRunning();
  }
}
