package app.osmosi.heater.api;

import java.io.File;
import java.io.IOException;
import java.util.List;

import app.osmosi.heater.adapters.Adapter;
import app.osmosi.heater.adapters.http.HttpAdapter;
import app.osmosi.heater.model.AppState;
import app.osmosi.heater.model.Floor;
import app.osmosi.heater.model.HotWater;
import app.osmosi.heater.model.HotWaterTimer;
import app.osmosi.heater.model.Switch;
import app.osmosi.heater.store.AppStatePersister;
import app.osmosi.heater.store.Store;
import app.osmosi.heater.store.actions.FloorUpdateAction;
import app.osmosi.heater.store.actions.HotWaterUpdateAction;
import app.osmosi.heater.store.actions.TimerUpdateAction;
import app.osmosi.heater.store.reducers.AppReducer;
import app.osmosi.heater.utils.Env;

public class Api {
  private static Store<AppState> store;
  private static List<Adapter> adapters;

  private Api() {
  }

  public static void init(Store<AppState> store, List<Adapter> adapters) {
    Api.store = store;
    Api.adapters = adapters;
    // Adapters:
    adapters.forEach(a -> a.addSubscribers(store));
  }

  public static void init() {
    AppReducer reducer = new AppReducer();
    try {
      AppState persisted = AppStatePersister.loadState();
      store = new Store<AppState>(persisted, reducer);
    } catch (IOException e) {
      System.out.println("Failed to load persisted state. Falling back to a clean state");
      store = new Store<AppState>(new AppState(new Floor("Cima", 0, 0, 99, Switch.OFF, "Suite", 2, 0),
          new Floor("Baixo", 0, 0, 99, Switch.OFF, "Sala", 3, 0),
          new HotWater(Switch.OFF),
          List.of()), reducer);
    }
    // Adapters:
    try {
      HttpAdapter httpAdapter = new HttpAdapter(new File(Env.CONFIG_PATH + "/http-adapter.json"));
      adapters = List.of(httpAdapter); // Multiple adapters can be combined here
      adapters.forEach(a -> a.addSubscribers(store));
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("Failed to load the adapters. Can't contiunue.");
      System.exit(1);
    }

    // Persist all changes of AppState:
    store.subscribe(s -> s, AppStatePersister::persist);
  }

  private static Floor updateHeaterState(final Floor f) {
    final double triggerTemp = f.getHeaterState() == Switch.ON ? f.getDesiredTemp() : f.getDesiredTemp() - 0.5;
    if (f.getActualTemp() < triggerTemp) {
      return f.withHeaterState(Switch.ON);// .setNextHeaterSchedule (ver codigo no clojure)
    }
    return f.withHeaterState(Switch.OFF);
  }

  public static void turnOnHotWater(final int timeoutMs) {
    store.dispatch(new HotWaterUpdateAction(new HotWater(Switch.ON)));
    final long instanceId = store.getState().getHotWater().getInstanceId();
    System.out.println("Hot water is ON for " + (timeoutMs / 60 / 1000) + " minutes");
    new Thread(() -> {
      try {
        Thread.sleep(timeoutMs);
      } catch (InterruptedException e) {
      }
      if (instanceId == store.getState().getHotWater().getInstanceId()) {
        turnOffHotWater();
      }
    }).start();
  }

  public static void turnOffHotWater() {
    store.dispatch(new HotWaterUpdateAction(new HotWater(Switch.OFF)));
    System.out.println("Hot water is OFF");
  }

  public static void updateFloor(final Floor newFloor) {
    Floor updatedFloor = updateHeaterState(newFloor);
    store.dispatch(new FloorUpdateAction(updatedFloor));
  }

  public static void syncAdapters() {
    System.out.println("Synchronizing all adapters");
    adapters.forEach(a -> a.sync(getCurrentState()));
    turnOffHotWater();
  }

  // TODO should this method exist? maybe would be better to expose the store.
  public static void setTimers(List<HotWaterTimer> timers) {
    store.dispatch(new TimerUpdateAction(timers));
  }

  public static AppState getCurrentState() {
    return store.getState();
  }
}
