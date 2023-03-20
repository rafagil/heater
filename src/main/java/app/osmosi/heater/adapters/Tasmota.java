package app.osmosi.heater.adapters;

import app.osmosi.heater.model.AppState;
import app.osmosi.heater.store.Store;

public class Tasmota implements Adapter {
    @Override
    public void addSubscribers(Store<AppState> store) {
        store.subscribe(s -> s.getBaixo().getHeaterState(), state -> System.out.println(state.getBaixo().getHeaterState()));
        store.subscribe(s -> s.getCima().getHeaterState(), state -> System.out.println(state.getCima().getHeaterState()));
    }
}
