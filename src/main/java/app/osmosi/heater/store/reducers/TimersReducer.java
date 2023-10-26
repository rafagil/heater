package app.osmosi.heater.store.reducers;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import app.osmosi.heater.model.HotWaterTimer;
import app.osmosi.heater.store.actions.Action;
import app.osmosi.heater.store.actions.AddTimerAction;
import app.osmosi.heater.store.actions.SingleTimerUpdateAction;
import app.osmosi.heater.store.actions.TimerUpdateAction;

public class TimersReducer implements Reducer<Set<HotWaterTimer>> {

	@Override
	public Set<HotWaterTimer> reduce(Action action, Set<HotWaterTimer> state) {
		if (action instanceof AddTimerAction addAction) {
			Set<HotWaterTimer> newSet = new HashSet<>(state);
			newSet.add(addAction.getData());
			return Collections.unmodifiableSet(newSet);
		}

		if (action instanceof TimerUpdateAction updateAction) {
			return updateAction.getData();
		}

		if (action instanceof SingleTimerUpdateAction updateAction) {
			Set<HotWaterTimer> newSet = new HashSet<>(state);
			newSet.remove(updateAction.getData());
			newSet.add(updateAction.getData());
			return Collections.unmodifiableSet(newSet);
		}

		return state;
	}
}
