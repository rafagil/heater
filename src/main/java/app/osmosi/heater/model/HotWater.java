package app.osmosi.heater.model;

import static app.osmosi.heater.utils.JsonObjectBuilder.*;

import java.io.Serializable;

public class HotWater implements JsonObject, Serializable {
	private static final long serialVersionUID = 1L;
	private Switch state;
	private final int sonoffChannel = 4;
	private static long instanceId = 0l;

	public HotWater(Switch state) {
		this.state = state;
		instanceId = instanceId + 1;
		if (instanceId > 1000) {
			instanceId = 0;
		}
	}

	public Switch getState() {
		return state;
	}

	public int getSonoffChannel() {
		return sonoffChannel;
	}

	public long getInstanceId() {
		return instanceId;
	}

	public String asJson() {
		return object(text("state", getState().toString()));
	}
}
