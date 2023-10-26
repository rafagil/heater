package app.osmosi.heater.store.actions;

import java.util.Set;

import app.osmosi.heater.model.HotWaterTimer;

public class TimerUpdateAction implements Action {
	private final Set<HotWaterTimer> data;

	public TimerUpdateAction(Set<HotWaterTimer> timers) {
		this.data = timers;
	}

	public Set<HotWaterTimer> getData() {
		return data;
	}
}
