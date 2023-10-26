package app.osmosi.heater.store.reducers;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import app.osmosi.heater.model.Floor;
import app.osmosi.heater.store.actions.Action;
import app.osmosi.heater.store.actions.FloorUpdateAction;

public class FloorsReducer implements Reducer<Set<Floor>> {
	@Override
	public Set<Floor> reduce(Action action, Set<Floor> state) {
		if (action instanceof FloorUpdateAction fua) {
			Set<Floor> newSet = new HashSet<>(state);
			newSet.remove(fua.getData());
			newSet.add(fua.getData());
			return Collections.unmodifiableSet(newSet);
		}
		return state;
	}
}
