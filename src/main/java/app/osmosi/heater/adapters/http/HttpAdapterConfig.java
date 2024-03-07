package app.osmosi.heater.adapters.http;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import app.osmosi.heater.utils.OrgTableParser;

import java.io.File;
import java.io.IOException;

public class HttpAdapterConfig {
	private final List<CentralHeatingConfig> centralHeating;
	private final RequestConfig hotWater;

	private RequestConfig parseRequest(Map<String, String> row) {
		String onURL = row.get("url on");
		String offURL = row.get("url off");
		String method = row.get("method");
		String onPayload = row.get("payload on");
		String offPayload = row.get("payload off");
		return new RequestConfig(onURL, offURL, method, onPayload, offPayload);
	}

	private CentralHeatingConfig parseCHConfig(Map<String, String> row) {
		String floorName = row.get("zone");
		String deviceName = row.get("device");
		RequestConfig request = parseRequest(row);
		return new CentralHeatingConfig(floorName, deviceName, request);
	}

	public HttpAdapterConfig(File centralHeatingFile, File hotWaterFile) throws IOException {
		OrgTableParser parser = new OrgTableParser();
		var ch = parser.parseFile(centralHeatingFile);
		var hw = parser.parseFile(hotWaterFile);

		this.centralHeating = ch.stream()
				.map(this::parseCHConfig)
				.collect(Collectors.toList());
		this.hotWater = parseRequest(hw.stream().findFirst().orElse(Map.of()));
	}

	public List<CentralHeatingConfig> getCentralHeating() {
		return centralHeating;
	}

	public RequestConfig getHotWater() {
		return hotWater;
	}
}
