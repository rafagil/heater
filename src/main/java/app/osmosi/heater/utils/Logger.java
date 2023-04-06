package app.osmosi.heater.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
  private static int level;

  private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

  private static void log(String severity, String text) {
    System.out.println(String.format("%s %s: %s", sdf.format(new Date()), severity, text));
  }

  public static void info(Object text) {
    if ((level & LogLevel.INFO.code) == LogLevel.INFO.code) {
      log("INFO", String.valueOf(text));
    }
  }

  public static void error(Object text) {
    if ((level & LogLevel.ERROR.code) == LogLevel.ERROR.code) {
      log("ERROR", String.valueOf(text));
    }
  }

  public static void debug(Object text) {
    if ((level & LogLevel.DEBUG.code) == LogLevel.DEBUG.code) {
      log("DEBUG", String.valueOf(text));
    }
  }

  public static void setLogLevel(LogLevel level) {
    int sum = 0;
    switch (level) {
      case DEBUG:
        sum += LogLevel.DEBUG.code;
      case INFO:
        sum += LogLevel.INFO.code;
      case ERROR:
        sum += LogLevel.ERROR.code;
    }
    Logger.level = sum;
  }
}
