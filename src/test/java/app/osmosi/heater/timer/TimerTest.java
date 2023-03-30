package app.osmosi.heater.timer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.Test;

import app.osmosi.heater.model.HotWaterTimer;

public class TimerTest {
  @Test
  public void getsTheCorrectTimer() {
    Timer timer = new Timer();
    List<HotWaterTimer> timers = List.of(new HotWaterTimer(7, 0, 10, 0),
        new HotWaterTimer(11, 15, 10, 0));

    assertTrue(timer.findTimer(7 * 60, timers).isPresent());
    assertTrue(timer.findTimer(11 * 60, timers).isPresent());
    assertFalse(timer.findTimer(6 * 60, timers).isPresent());
  }

  @Test
  public void ignoresAlreadyTriggered() {
    int today = LocalDateTime.now().getDayOfMonth();
    Timer timer = new Timer();
    List<HotWaterTimer> timers = List.of(new HotWaterTimer(7, 0, 10, today),
        new HotWaterTimer(11, 15, 10, today));

    assertFalse(timer.findTimer(7 * 60, timers).isPresent());
    assertFalse(timer.findTimer(11 * 60, timers).isPresent());
  }
}
