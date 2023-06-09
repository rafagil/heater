package app.osmosi.heater.timer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import app.osmosi.heater.model.HotWaterTimer;
import app.osmosi.heater.utils.FileUtils;

public class HotWaterTimerParser {

  private static Predicate<String> comments = l -> !l.startsWith("#");
  private static Function<String, HotWaterTimer> toTimer = l -> {
    String[] items = l.trim().split(",");
    Integer hours = Integer.valueOf(items[0].trim());
    Integer minutes = Integer.valueOf(items[1].trim());
    Integer timeout = Integer.valueOf(items[2].trim()) * 60 * 1000;
    return new HotWaterTimer(hours, minutes, timeout);
  };

  public static Set<HotWaterTimer> parse(Stream<String> lines) {
    return lines
        .filter(comments)
        .map(toTimer)
        .collect(Collectors.toSet());
  }

  public static Set<HotWaterTimer> parse(File file) throws FileNotFoundException, IOException {
    Stream<String> stream = FileUtils.read(file);
    Set<HotWaterTimer> items = parse(stream);
    stream.close();
    return items;
  }
}
