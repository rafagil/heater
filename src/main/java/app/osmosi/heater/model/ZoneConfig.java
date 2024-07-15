package app.osmosi.heater.model;


public class ZoneConfig {
	private final String zone;
	private final Device device;
	private final boolean enabled;

	public ZoneConfig(String zone, Device device, boolean enabled) {
		this.zone = zone;
		this.device = device;
		this.enabled = enabled;
	}

	public String getZone() {
		return zone;
	}
	public Device getDevice() {
		return device;
	}
	public boolean isEnabled() {
		return enabled;
	}

	public String toString() {
		return "{zone: " + zone + ",device: " + device + ",enabled: " + enabled + "}";
	}
}
