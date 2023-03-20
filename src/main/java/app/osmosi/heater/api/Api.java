package app.osmosi.heater.api;

import app.osmosi.heater.adapters.Adapter;
import app.osmosi.heater.adapters.Sonoff;
import app.osmosi.heater.adapters.Tasmota;
import app.osmosi.heater.model.AppState;
import app.osmosi.heater.model.Floor;
import app.osmosi.heater.model.HotWater;
import app.osmosi.heater.model.Switch;
import app.osmosi.heater.store.Store;
import app.osmosi.heater.store.actions.FloorUpdateAction;
import app.osmosi.heater.store.actions.HotWaterUpdateAction;
import app.osmosi.heater.store.reducers.AppReducer;

public class Api {
    private static Adapter adapter;
    private static Store<AppState> store;

    private Api() {}

    public static void init() {
        AppReducer reducer = new AppReducer();
        store = new Store<AppState>(new AppState(new Floor("Cima", 0, 0, 99, Switch.OFF, "Suite", 2, 0),
                                                 new Floor("Baixo", 0, 0, 99, Switch.OFF, "Sala", 3, 0),
                                                 new HotWater(Switch.OFF)), reducer);
        adapter = new Sonoff();
        adapter.addSubscribers(store);
        new Tasmota().addSubscribers(store); // Combo temp implementation
    }

    private static Floor updateHeaterState(final Floor f) {
        final double triggerTemp = f.getHeaterState() == Switch.ON ? f.getDesiredTemp() : f.getDesiredTemp() - 0.5;
        if (f.getActualTemp() < triggerTemp) {
            return f.setHeaterState(Switch.ON);// .setNextHeaterSchedule (ver codigo no clojure)
        }
        return f.setHeaterState(Switch.OFF);
    }

    public static void turnOnWotWater(final int minutes) {
        final long instanceId = store.getState().getHotWater().getInstanceId();
        store.dispatch(new HotWaterUpdateAction(new HotWater(Switch.ON)));
        System.out.println("Hot water is ON");
        new Thread(() -> {
            try{
                Thread.sleep(60 * 1000 * minutes);
            } catch (InterruptedException e) {}
            if (instanceId == store.getState().getHotWater().getInstanceId()) {
                turnOffHotWater();
            }
        });
    }

    public static void turnOffHotWater() {
        store.dispatch(new HotWaterUpdateAction(new HotWater(Switch.OFF)));
        System.out.println("Hot water is OFF");
    }

    public static void updateFloor(final Floor newFloor) {
        Floor updatedFloor = updateHeaterState(newFloor);
        store.dispatch(new FloorUpdateAction(updatedFloor));
    }

    public static void switchAllOff() {
        System.out.println("Switching all OFF");
        Floor baixo = store.getState().getBaixo().setHeaterState(Switch.OFF);
        Floor cima = store.getState().getCima().setHeaterState(Switch.OFF);
        store.dispatch(new FloorUpdateAction(baixo));
        store.dispatch(new FloorUpdateAction(cima));
        turnOffHotWater();
    }

    public static AppState getCurrentState() {
        return store.getState();
    }
}
