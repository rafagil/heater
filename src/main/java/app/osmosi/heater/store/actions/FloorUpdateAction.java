package app.osmosi.heater.store.actions;

import app.osmosi.heater.model.Floor;

public class FloorUpdateAction implements Action {
	private final Floor data;

	public FloorUpdateAction(Floor data) {
		this.data = data;
	}

	public Floor getData() {
		return data;
	}
}
