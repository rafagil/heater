package app.osmosi.heater.scheduler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import app.osmosi.heater.model.Device;
import app.osmosi.heater.model.ScheduleItem;
import app.osmosi.heater.utils.FileUtils;

public class ScheduleParser {

	private static Predicate<String> comments = l -> !l.startsWith("#");
	private static Predicate<String> emptyLines = l -> !l.trim().equals("");
	private static Function<String, DayOfWeek> toDayOfWeek = day -> DayOfWeek.of(Integer.valueOf(day));
	private static Function<String, Device> toDevice = d -> Device.valueOf(d);

	private static Function<String, ScheduleItem> toScheduleItem = l -> {
		String[] items = l.trim().split(",");
		String floorName = items[0].trim();
		String[] time = items[1].trim().split(":");
		Double desiredTemp = Double.valueOf(items[2].trim());
		Integer hours = Integer.valueOf(time[0]);
		Integer minutes = Integer.valueOf(time[1]);
		Set<DayOfWeek> daysOfWeek = Stream.of(items[3].split(" "))
				.map(toDayOfWeek)
				.collect(Collectors.toSet());
		Set<Device> devices = Stream.of(items[4].split(" "))
				.map(toDevice)
				.collect(Collectors.toSet());
		return new ScheduleItem(floorName, hours, minutes, desiredTemp, daysOfWeek, devices);
	};

	public static List<ScheduleItem> parse(Stream<String> lines) {
		return lines
				.filter(comments)
				.filter(emptyLines)
				.map(toScheduleItem)
				.collect(Collectors.toList());
	}

	public static List<ScheduleItem> parse(File file) throws FileNotFoundException, IOException {
		Stream<String> stream = FileUtils.read(file);
		List<ScheduleItem> items = parse(stream);
		stream.close();
		return items;
	}
}
