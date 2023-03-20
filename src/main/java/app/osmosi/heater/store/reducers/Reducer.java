package app.osmosi.heater.store.reducers;

import app.osmosi.heater.store.actions.Action;

@FunctionalInterface
public interface Reducer<State> {
    State reduce(Action action, State state);
}
