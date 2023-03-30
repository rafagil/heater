package app.osmosi.heater.api;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import app.osmosi.heater.adapters.Adapter;
import app.osmosi.heater.model.AppState;
import app.osmosi.heater.model.Floor;
import app.osmosi.heater.model.HotWater;
import app.osmosi.heater.model.Switch;
import app.osmosi.heater.store.Store;
import app.osmosi.heater.store.reducers.AppReducer;

public class ApiTest {
  private Adapter emptyAdapter = new Adapter() {
    public void addSubscribers(Store<AppState> store) {
    }

    public void sync(AppState state) {
    }
  };

  @Test
  public void hotWaterTurnsOffAfterTimeout() {
    AppReducer reducer = new AppReducer();
    Store<AppState> store = new Store<AppState>(new AppState(new Floor("Cima", 0, 0, 99, Switch.OFF, "Suite", 2, 0),
        new Floor("Baixo", 0, 0, 99, Switch.OFF, "Sala", 3, 0),
        new HotWater(Switch.OFF),
        List.of()), reducer);

    Api.init(store, List.of(emptyAdapter));
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
    AppReducer reducer = new AppReducer();
    Store<AppState> store = new Store<AppState>(new AppState(new Floor("Cima", 0, 0, 99, Switch.OFF, "Suite", 2, 0),
        new Floor("Baixo", 0, 0, 99, Switch.OFF, "Sala", 3, 0),
        new HotWater(Switch.OFF),
        List.of()), reducer);

    Api.init(store, List.of(emptyAdapter));
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
    Store<AppState> store = new Store<AppState>(new AppState(new Floor("Cima", 20, 0, 99, Switch.OFF, "Suite", 2, 0),
        new Floor("Baixo", 0, 0, 99, Switch.OFF, "Sala", 3, 0),
        new HotWater(Switch.OFF),
        List.of()), reducer);

    Api.init(store, List.of(emptyAdapter));
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
    Store<AppState> store = new Store<AppState>(new AppState(new Floor("Cima", 20, 19, 99, Switch.OFF, "Suite", 2, 0),
        new Floor("Baixo", 0, 0, 99, Switch.OFF, "Sala", 3, 0),
        new HotWater(Switch.OFF),
        List.of()), reducer);

    Api.init(store, List.of(emptyAdapter));
    assertEquals(Switch.OFF, Api.getCurrentState().getFloorByName("Cima").getHeaterState());
    Api.updateFloor(Api.getCurrentState().getFloorByName("Cima").withActualTemp(10));
    assertEquals(Switch.ON, Api.getCurrentState().getFloorByName("Cima").getHeaterState());
    Api.updateFloor(Api.getCurrentState().getFloorByName("Cima").withActualTemp(19));
    assertEquals(Switch.OFF, Api.getCurrentState().getFloorByName("Cima").getHeaterState());
  }
}
