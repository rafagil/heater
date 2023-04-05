package app.osmosi.heater.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
  // TODO Add a logLevel variable to change the log level

  private static void log(String severity, String text) {
    String now = new SimpleDateFormat().format(new Date());
    System.out.println(severity + " " + now + ": " + text);
  }

  public static void info(String text) {
    log("INFO", text);
  }

  public static void error(String text) {
    log("ERROR", text);
  }

  public static void debug(String text) {
    log("DEBUG", text);
  }
}
