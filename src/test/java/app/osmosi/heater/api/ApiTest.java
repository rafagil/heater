package app.osmosi.heater.api;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Set;

import org.junit.Test;

import app.osmosi.heater.model.AppState;
import app.osmosi.heater.model.Floor;
import app.osmosi.heater.model.HotWater;
import app.osmosi.heater.model.Switch;
import app.osmosi.heater.store.Store;
import app.osmosi.heater.store.reducers.AppReducer;

public class ApiTest {

  private Store<AppState> getInitialStore() {
    AppReducer reducer = new AppReducer();
    return new Store<AppState>(new AppState(
        new HotWater(Switch.OFF),
        Set.of(),
        new Floor("Cima", 0, 0, 99, Switch.OFF, 0),
        new Floor("Baixo", 0, 0, 99, Switch.OFF, 0)), reducer);
  }

  @Test
  public void hotWaterTurnsOffAfterTimeout() {
    Store<AppState> store = getInitialStore();
    Api.init(store, List.of());
    assertEquals(Switch.OFF, Api.getCurrentState().getHotWater().getState());
    Api.turnOnHotWater(500);
    assertEquals(Switch.ON, Api.getCurrentState().getHotWater().getState());
    try {
      Thread.sleep(600);
    } catch (InterruptedException e) {
    }
    assertEquals(Switch.OFF, Api.getCurrentState().getHotWater().getState());
  }

  @Test
  public void secondTimerOverridesFirst() {
    Store<AppState> store = getInitialStore();
    Api.init(store, List.of());
    assertEquals(Switch.OFF, Api.getCurrentState().getHotWater().getState());
    Api.turnOnHotWater(500);
    assertEquals(Switch.ON, Api.getCurrentState().getHotWater().getState());
    try {
      Thread.sleep(300);
      Api.turnOnHotWater(600);

    } catch (InterruptedException e) {
    }

    try {
      Thread.sleep(400);
    } catch (InterruptedException e) {
    }
    assertEquals(Switch.ON, Api.getCurrentState().getHotWater().getState());

    try {
      Thread.sleep(300);
    } catch (InterruptedException e) {
    }
    assertEquals(Switch.OFF, Api.getCurrentState().getHotWater().getState());
  }

  @Test
  public void switchesHeaterStates() {
    AppReducer reducer = new AppReducer();
    Store<AppState> store = new Store<AppState>(new AppState(
        new HotWater(Switch.OFF),
        Set.of(),
        new Floor("Cima", 20, 0, 99, Switch.OFF, 0),
        new Floor("Baixo", 0, 0, 99, Switch.OFF, 0)), reducer);

    Api.init(store, List.of());
    assertEquals(Switch.OFF, Api.getCurrentState().getFloorByName("Cima").getHeaterState());
    Api.updateFloor(Api.getCurrentState().getFloorByName("Cima").withActualTemp(10));
    assertEquals(Switch.ON, Api.getCurrentState().getFloorByName("Cima").getHeaterState());
    Api.updateFloor(Api.getCurrentState().getFloorByName("Cima").withActualTemp(20));
    assertEquals(Switch.OFF, Api.getCurrentState().getFloorByName("Cima").getHeaterState());
    Api.updateFloor(Api.getCurrentState().getFloorByName("Cima").withActualTemp(19.5));
    assertEquals(Switch.OFF, Api.getCurrentState().getFloorByName("Cima").getHeaterState());
    Api.updateFloor(Api.getCurrentState().getFloorByName("Cima").withActualTemp(19.4));
    assertEquals(Switch.ON, Api.getCurrentState().getFloorByName("Cima").getHeaterState());
  }

  @Test
  public void setBackTempShouldOverrideDesiredTemp() {
    AppReducer reducer = new AppReducer();
    Store<AppState> store = new Store<AppState>(new AppState(
        new HotWater(Switch.OFF),
        Set.of(),
        new Floor("Cima", 20, 19, 99, Switch.OFF, 0),
        new Floor("Baixo", 0, 0, 99, Switch.OFF, 0)), reducer);

    Api.init(store, List.of());
    assertEquals(Switch.OFF, Api.getCurrentState().getFloorByName("Cima").getHeaterState());
    Api.updateFloor(Api.getCurrentState().getFloorByName("Cima").withActualTemp(10));
    assertEquals(Switch.ON, Api.getCurrentState().getFloorByName("Cima").getHeaterState());
    Api.updateFloor(Api.getCurrentState().getFloorByName("Cima").withActualTemp(19));
    assertEquals(Switch.OFF, Api.getCurrentState().getFloorByName("Cima").getHeaterState());
  }

}
