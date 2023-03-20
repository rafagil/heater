package app.osmosi.heater.adapters;

import java.util.function.Consumer;

import app.osmosi.heater.model.AppState;
import app.osmosi.heater.model.Floor;
import app.osmosi.heater.model.HotWater;
import app.osmosi.heater.model.Switch;
import app.osmosi.heater.store.Store;

public class Sonoff implements Adapter {

    private void turnOn(int sonoffChannel) {
        System.out.println("Turning ON channel " + sonoffChannel);
    }

    private void turnOff(int sonoffChannel) {
        System.out.println("Turning OFF channel " + sonoffChannel);
    }

    @Override
    public void addSubscribers(Store<AppState> store) {
        Consumer<Floor> handleFloor = f -> {
            if (f.getHeaterState() == Switch.ON) {
                turnOn(f.getSonoffChannel());
            } else {
                turnOff(f.getSonoffChannel());
            }
        };
        Consumer<HotWater> handleHw = hw -> {
            if (hw.getState() == Switch.ON) {
                turnOn(hw.getSonoffChannel());
            } else {
                turnOff(hw.getSonoffChannel());
            }
        };
        store.subscribe(s -> s.getBaixo().getHeaterState(), appState -> handleFloor.accept(appState.getBaixo()));
        store.subscribe(s -> s.getCima().getHeaterState(), appState -> handleFloor.accept(appState.getCima()));
        store.subscribe(s -> s.getHotWater().getState(), appState -> handleHw.accept(appState.getHotWater()));
    }
}
