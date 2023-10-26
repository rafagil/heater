package app.osmosi.heater.store.actions;

import app.osmosi.heater.model.HotWaterTimer;

public class AddTimerAction implements Action {
	private final HotWaterTimer data;

	public AddTimerAction(HotWaterTimer timer) {
		this.data = timer;
	}

	public HotWaterTimer getData() {
		return data;
	}
}
