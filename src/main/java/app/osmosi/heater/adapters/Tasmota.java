package app.osmosi.heater.adapters;

import app.osmosi.heater.model.AppState;
import app.osmosi.heater.model.Floor;
import app.osmosi.heater.model.Switch;
import app.osmosi.heater.store.Store;

public class Tasmota implements Adapter {

    private void syncFloor(Floor f) {
        if (f.getHeaterState() == Switch.ON) {
            System.out.println("Switching Tasmota " + f.getName() + "ON");
        } else {
            System.out.println("Switching Tasmota " + f.getName() + "OFF");
        }
    }

    @Override
    public void addSubscribers(Store<AppState> store) {
        store.subscribe(s -> s.getBaixo().getHeaterState(), state -> System.out.println(state.getBaixo().getHeaterState()));
        store.subscribe(s -> s.getCima().getHeaterState(), state -> System.out.println(state.getCima().getHeaterState()));
    }

    @Override
    public void sync(AppState state) {
        state.getFloors().forEach(f -> syncFloor(f));
    }
}
