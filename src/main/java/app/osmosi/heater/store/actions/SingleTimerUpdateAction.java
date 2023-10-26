package app.osmosi.heater.store.actions;

import app.osmosi.heater.model.HotWaterTimer;

public class SingleTimerUpdateAction implements Action {
	private final HotWaterTimer data;

	public SingleTimerUpdateAction(HotWaterTimer timer) {
		this.data = timer;
	}

	public HotWaterTimer getData() {
		return data;
	}
}
