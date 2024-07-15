package app.osmosi.heater.api;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import app.osmosi.heater.model.Device;
import app.osmosi.heater.model.ZoneConfig;
import app.osmosi.heater.utils.OrgTableParser;

public class ZoneConfigParser {

	private static Function<Map<String, String>, ZoneConfig> toZoneConfig = row -> {
		String zone = row.get("zone");
		String device = row.get("device");
		String enabled = row.get("enabled");
		return new ZoneConfig(zone, Device.valueOf(device), enabled.toLowerCase().equals("yes"));
	};

	public static List<ZoneConfig> parse(File file) throws IOException {
		OrgTableParser parser = new OrgTableParser();
		return parser.parseFile(file).stream()
				.map(toZoneConfig)
				.collect(Collectors.toList());
	}
}
