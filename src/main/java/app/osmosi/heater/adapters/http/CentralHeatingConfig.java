package app.osmosi.heater.adapters.http;

public class CentralHeatingConfig {
	private final String floorName;
	private final String deviceName;
	private final RequestConfig request;

	public CentralHeatingConfig(String floorName, String deviceName, RequestConfig request) {
		this.floorName = floorName;
		this.request = request;
		this.deviceName = deviceName;
	}

	public String getFloorName() {
		return floorName;
	}

	public RequestConfig getRequest() {
		return request;
	}

	public String getDeviceName() {
		return deviceName;
	}
}
