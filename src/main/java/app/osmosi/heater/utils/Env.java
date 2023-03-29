package app.osmosi.heater.utils;

import java.util.Optional;

public class Env {
  public static final boolean DEBUG = Optional.ofNullable(System.getenv("DEBUG")).isPresent();
  public static final String CONFIG_PATH = getEnv("CONFIG_PATH", "/projects/heater/config");
  public static final String DB_PATH = getEnv("DB_PATH", "/projects/heater/db");

  private static String getEnv(String key, String defaultValue) {
    return Optional.ofNullable(System.getenv(key)).orElse(defaultValue);
  }
}
