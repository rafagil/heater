package app.osmosi.heater.scheduler;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Set;

import org.junit.Test;

import app.osmosi.heater.api.Api;
import app.osmosi.heater.model.AppState;
import app.osmosi.heater.model.Floor;
import app.osmosi.heater.model.HotWater;
import app.osmosi.heater.model.ScheduleItem;
import app.osmosi.heater.model.Switch;
import app.osmosi.heater.store.Store;
import app.osmosi.heater.store.reducers.AppReducer;

public class SchedulerTest {
  @Test
  public void getsTheCorrectScheduleItem() {
    List<ScheduleItem> schedule = List.of(new ScheduleItem("Cima", 7, 0, 19),
        new ScheduleItem("Cima", 18, 20, 21),
        new ScheduleItem("Cima", 23, 0, 20.3),
        new ScheduleItem("Baixo", 7, 0, 21),
        new ScheduleItem("Baixo", 22, 0, 20),
        new ScheduleItem("Baixo", 23, 0, 18));
    Scheduler s = new Scheduler();
    assertEquals(19, s.findScheduleItem(7 * 60, schedule, "Cima").getDesiredTemp(), 0); // 7:00
    assertEquals(21, s.findScheduleItem(7 * 60, schedule, "Baixo").getDesiredTemp(), 0); // 7:00

    assertEquals(19, s.findScheduleItem(12 * 60, schedule, "Cima").getDesiredTemp(), 0); // 12:00
    assertEquals(21, s.findScheduleItem(12 * 60, schedule, "Baixo").getDesiredTemp(), 0); // 12:00

    assertEquals(21, s.findScheduleItem((22 * 60) + 59, schedule, "Cima").getDesiredTemp(), 0); // 22:59
    assertEquals(20, s.findScheduleItem((22 * 60) + 59, schedule, "Baixo").getDesiredTemp(), 0); // 22:59

    assertEquals(20.3, s.findScheduleItem(0, schedule, "Cima").getDesiredTemp(), 0); // 0:00
    assertEquals(18, s.findScheduleItem(0, schedule, "Baixo").getDesiredTemp(), 0); // 0:00

    assertEquals(20.3, s.findScheduleItem(3 * 60, schedule, "Cima").getDesiredTemp(), 0); // 3:00
    assertEquals(18, s.findScheduleItem(3 * 60, schedule, "Baixo").getDesiredTemp(), 0); // 3:00
  }

  @Test
  public void doesNotOverrideCustomDesiredTemp() {
    List<ScheduleItem> schedule = List.of(new ScheduleItem("Cima", 7, 0, 17));

    AppReducer reducer = new AppReducer();
    Store<AppState> store = new Store<AppState>(new AppState(new Floor("Cima", 0, 0, 99, Switch.OFF, "Suite", 2, 0),
        new Floor("Baixo", 0, 0, 99, Switch.OFF, "Sala", 3, 0),
        new HotWater(Switch.OFF),
        Set.of()), reducer);

    Api.init(store, List.of());

    Scheduler s = new Scheduler();
    s.updateDesiredTemp(schedule, 1, 7 * 60);
    // Calling it once should change the getDesiredTemp:
    assertEquals(17, Api.getCurrentState().getFloorByName("Cima").getDesiredTemp(), 0);
    Api.updateFloor(Api.getCurrentState().getFloorByName("Cima").withDesiredTemp(20));
    assertEquals(20, Api.getCurrentState().getFloorByName("Cima").getDesiredTemp(), 0);
    // Calling it twice should not update the temperature:
    s.updateDesiredTemp(schedule, 1, 7 * 60);
    assertEquals(20, Api.getCurrentState().getFloorByName("Cima").getDesiredTemp(), 0);

    // Calling on another day should override the temperature:
    s.updateDesiredTemp(schedule, 2, 7 * 60);
    assertEquals(17, Api.getCurrentState().getFloorByName("Cima").getDesiredTemp(), 0);
  }
}
