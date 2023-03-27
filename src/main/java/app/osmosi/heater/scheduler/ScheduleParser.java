package app.osmosi.heater.scheduler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import app.osmosi.heater.model.ScheduleItem;
import app.osmosi.heater.utils.FileUtils;

public class ScheduleParser {

    private static Predicate<String> comments = l -> !l.startsWith("#");
    private static Function<String, ScheduleItem> toScheduleItem = l -> {
        String[] items = l.trim().split(",");
        String floorName = items[0].trim();
        String[] time = items[1].trim().split(":");
        Double desiredTemp = Double.valueOf(items[2].trim());
        Integer hours = Integer.valueOf(time[0]);
        Integer minutes = Integer.valueOf(time[1]);
        return new ScheduleItem(floorName, hours, minutes, desiredTemp);
    };

    public static List<ScheduleItem> parse(Stream<String> lines) {
        return lines
                .filter(comments)
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
