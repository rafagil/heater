package app.osmosi.heater.store.reducers;

import app.osmosi.heater.model.Floor;
import app.osmosi.heater.store.actions.Action;
import app.osmosi.heater.store.actions.FloorUpdateAction;

public class FloorReducer implements Reducer<Floor> {
    private final String name;

    public FloorReducer(String name) {
        this.name = name;
    }

    @Override
    public Floor reduce(Action action, Floor state) {
        if (action instanceof FloorUpdateAction fua && name.equals(fua.getData().getName())) {
            return fua.getData();
        }
        return state;
    }
}
