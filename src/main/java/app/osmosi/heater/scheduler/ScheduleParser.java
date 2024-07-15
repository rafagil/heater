package app.osmosi.heater.scheduler;

import java.io.File;
import java.io.IOException;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import app.osmosi.heater.model.Device;
import app.osmosi.heater.model.ScheduleItem;
import app.osmosi.heater.utils.OrgTableParser;

public class ScheduleParser {

	private static Function<String, DayOfWeek> toDayOfWeek = day -> DayOfWeek.of(Integer.valueOf(day));
	private static Function<String, Device> toDevice = d -> Device.valueOf(d);

	private static Function<Map<String, String>, ScheduleItem> toScheduleItem = row -> {
		String floorName = row.get("zone");
		String[] time = row.get("time").split(":");
		Double desiredTemp = Double.valueOf(row.get("desired temp"));
		Integer hours = Integer.valueOf(time[0]);
		Integer minutes = Integer.valueOf(time[1]);
		Set<DayOfWeek> daysOfWeek = Stream.of(row.get("days of week").split(" "))
				.map(toDayOfWeek)
				.collect(Collectors.toSet());
		Set<Device> devices = Stream.of(row.get("devices").split(" "))
				.map(toDevice)
				.collect(Collectors.toSet());
		return new ScheduleItem(floorName, hours, minutes, desiredTemp, daysOfWeek, devices);
	};

	public static List<ScheduleItem> parse(File file) throws IOException {
		OrgTableParser parser = new OrgTableParser();
		return parser.parseFile(file).stream()
				.map(toScheduleItem)
				.collect(Collectors.toList());
	}
}
