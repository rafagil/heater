package app.osmosi.heater.timer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import app.osmosi.heater.model.HotWaterTimer;
import app.osmosi.heater.utils.FileUtils;

public class HotWaterTimerParser {
  private static int idCounter = 0;

  private static Predicate<String> comments = l -> !l.startsWith("#");
  private static Function<String, HotWaterTimer> toTimer = l -> {
    String[] items = l.trim().split(",");
    Integer hours = Integer.valueOf(items[0].trim());
    Integer minutes = Integer.valueOf(items[1].trim());
    Integer timeout = Integer.valueOf(items[2].trim()) * 60 * 1000;
    idCounter += 1;
    return new HotWaterTimer(idCounter, hours, minutes, timeout, 0);
  };

  public static List<HotWaterTimer> parse(Stream<String> lines) {
    return lines
        .filter(comments)
        .map(toTimer)
        .collect(Collectors.toList());
  }

  public static List<HotWaterTimer> parse(File file) throws FileNotFoundException, IOException {
    Stream<String> stream = FileUtils.read(file);
    List<HotWaterTimer> items = parse(stream);
    stream.close();
    return items;
  }
}
