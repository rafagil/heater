package app.osmosi.heater.model;

import static app.osmosi.heater.utils.JsonObjectBuilder.*;

public class HomebridgeStatus implements JsonObject {

	private int targetHeatingCoolingState;
	private double targetTemperature;
	private int currentHeatingCoolingState;
	private double currentTemperature;

	private int stateFromSwitch(Switch theSwitch) {
		// valid states are 0 = OFF, 1 = Heat, 2 = Cool, 3 = Auto
		return theSwitch == Switch.ON ? 1 : 0;
	}

	public HomebridgeStatus(Floor floor) {
		this.targetHeatingCoolingState = 3; // AUTO
		this.targetTemperature = floor.getDesiredTemp();
		this.currentHeatingCoolingState = stateFromSwitch(floor.getHeaterState());
		this.currentTemperature = floor.getActualTemp();
	}

	@Override
	public String asJson() {
		return object(
				number("targetHeatingCoolingState", targetHeatingCoolingState),
				number("targetTemperature", targetTemperature),
				number("currentHeatingCoolingState", currentHeatingCoolingState),
				number("currentTemperature", currentTemperature));
	}
}
