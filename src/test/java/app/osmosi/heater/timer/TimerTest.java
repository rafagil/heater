package app.osmosi.heater.timer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import app.osmosi.heater.api.Api;
import app.osmosi.heater.model.AppState;
import app.osmosi.heater.model.Floor;
import app.osmosi.heater.model.HotWater;
import app.osmosi.heater.model.HotWaterTimer;
import app.osmosi.heater.model.Switch;
import app.osmosi.heater.store.Store;
import app.osmosi.heater.store.reducers.AppReducer;

public class TimerTest {
  @Test
  public void getsTheCorrectTimer() {
    Timer timer = new Timer();
    Set<HotWaterTimer> timers = Set.of(new HotWaterTimer(7, 0, 10, 0),
        new HotWaterTimer(11, 15, 10, 0));

    assertTrue(timer.findTimer(7 * 60, timers).isPresent());
    assertTrue(timer.findTimer(11 * 60, timers).isPresent());
    assertFalse(timer.findTimer(6 * 60, timers).isPresent());
  }

  @Test
  public void ignoresAlreadyTriggered() {
    int today = LocalDateTime.now().getDayOfMonth();
    Timer timer = new Timer();
    Set<HotWaterTimer> timers = Set.of(new HotWaterTimer(7, 0, 10, today),
        new HotWaterTimer(11, 15, 10, today));

    assertFalse(timer.findTimer(7 * 60, timers).isPresent());
    assertFalse(timer.findTimer(11 * 60, timers).isPresent());
  }

  @Test
  public void updatesDayTriggered() {
    int today = LocalDateTime.now().getDayOfMonth();
    Timer timer = new Timer();
    Set<HotWaterTimer> timers = Set.of(new HotWaterTimer(7, 0, 10, 0));
    AppReducer reducer = new AppReducer();
    Store<AppState> store = new Store<AppState>(new AppState(new Floor("Cima", 0, 0, 99, Switch.OFF, "Suite", 2, 0),
        new Floor("Baixo", 0, 0, 99, Switch.OFF, "Sala", 3, 0),
        new HotWater(Switch.OFF),
        timers), reducer);

    Api.init(store, List.of());
    assertEquals(0, Api.getCurrentState().getTimers().stream().findFirst().get().getDayTriggered());

    timer.updateHotWaterState(7 * 60, today);
    assertEquals(today, Api.getCurrentState().getTimers().stream().findFirst().get().getDayTriggered());
  }
}
