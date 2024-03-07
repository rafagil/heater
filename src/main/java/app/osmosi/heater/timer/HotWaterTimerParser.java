package app.osmosi.heater.timer;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import app.osmosi.heater.model.HotWaterTimer;
import app.osmosi.heater.utils.OrgTableParser;

public class HotWaterTimerParser {

	private static Function<Map<String, String>, HotWaterTimer> toTimer = row -> {
		String[] time = row.get("time").split(":");
		Integer hours = Integer.valueOf(time[0]);
		Integer minutes = Integer.valueOf(time[1]);
		Integer timeout = Integer.valueOf(row.get("duration")) * 60 * 1000;
		return new HotWaterTimer(hours, minutes, timeout);
	};

	public static Set<HotWaterTimer> parse(File file) throws IOException {
		OrgTableParser parser = new OrgTableParser();
		return parser.parseFile(file).stream()
				.map(toTimer)
				.collect(Collectors.toSet());
	}
}
