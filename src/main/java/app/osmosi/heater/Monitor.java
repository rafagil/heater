package app.osmosi.heater;

import static app.osmosi.heater.utils.MobileNotification.sendNotification;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import app.osmosi.heater.api.Api;
import app.osmosi.heater.model.AppState;
import app.osmosi.heater.model.Floor;
import app.osmosi.heater.model.Switch;
import app.osmosi.heater.utils.Env;
import app.osmosi.heater.utils.FileUtils;
import app.osmosi.heater.utils.IntervalThread;
import app.osmosi.heater.utils.Logger;

public class Monitor {
  private static List<IntervalThread> threads;
  public static final String BALANCE_FILE_PATH = Env.DB_PATH + "/balance.txt";
  private static final double LOW_CREDIT_THRESHOLD = 12;
  // TODO: Think of a better way to implment these vars:
  static int minuteCounter = 0;
  static boolean sentHeaterOn = false;
  static boolean sentCredits = false;
  static double lastBalance = 0;

  private static int minutesToMs(int minutes) {
    return minutes * 60 * 1000;
  }

  private static void notifyAndShutDown(Floor f) {
    String msg = "Temperature not changing on " + f.getName();
    System.out.println(msg);
    sendNotification(msg);
    Api.updateFloor(f.withActualTemp(999));
  }

  /**
   * Returns true if any of the Switches is turned ON.
   */
  private static boolean isHeaterOn() {
    AppState state = Api.getCurrentState();
    boolean floorIsOn = state.getFloors().stream()
        .anyMatch(f -> f.getHeaterState() == Switch.ON);
    return floorIsOn || state.getHotWater().getState() == Switch.ON;
  }

  private static IntervalThread monitorCredits() {
    final int hourlyRate = 1; // 1 EUR per how (approx)
    final double minuteRate = hourlyRate / 60;
    final int timeout = minutesToMs(1);
    IntervalThread it = new IntervalThread(() -> {
      try (Stream<String> lines = FileUtils.read(BALANCE_FILE_PATH)) {
        Optional<String> line = lines.findFirst();
        double balance = Double.valueOf(line.orElse("0"));

        if (isHeaterOn()) {
          FileUtils.write(BALANCE_FILE_PATH, String.valueOf(balance - minuteRate));
        }

        if (!sentCredits && balance < LOW_CREDIT_THRESHOLD) {
          sendNotification("Running out of gas credits!");
          sentCredits = true;
        }

        if (balance > lastBalance) {
          sentCredits = false;
        }

      } catch (IOException e) {
        String msg = "Could not update current balance.";
        sendNotification(msg);
        System.out.println(msg);
      }
    }, timeout);
    return it;
  }

  /**
   * Checks for how long the heater is turned on.
   * it will send a notification when the number of 'ticks' exceeds the warning
   * limit.
   * it will shut down the heating when it exceeds the critical limit.
   */
  private static IntervalThread monitorOnStatus() {
    final int timeout = minutesToMs(1);
    final int warningLimit = 120; // 2h
    final int criticalLimit = 240; // 4h

    IntervalThread it = new IntervalThread(() -> {
      if (isHeaterOn()) {
        minuteCounter += 1;
      } else {
        minuteCounter = 0;
        sentHeaterOn = false;
      }
      if (minuteCounter > criticalLimit) {
        sendNotification("Critical: Heater will be shut down. Use 'Back Home' to turn it on again.");
        Api.getCurrentState().getFloors().forEach(f -> Api.updateFloor(f.withSetBackTemp(1)));
        Api.turnOffHotWater();
      } else if (!sentHeaterOn && minuteCounter > warningLimit) {
        sendNotification("Warning: Heater is been ON for " + (warningLimit / 60) + "h");
      }
    }, timeout);
    return it;
  }

  /**
   * Checks when was the last time the temperature was updated
   * if it wasn't updated after the timeout, it will turn off the heating.
   */
  private static IntervalThread monitorThermometers() {
    int timeout = minutesToMs(30);
    Map<String, Long> lastUpdate = new HashMap<>();
    IntervalThread t = new IntervalThread(() -> {
      AppState state = Api.getCurrentState();
      state.getFloors().forEach(f -> {
        if (lastUpdate.get(f.getName()) != null && lastUpdate.get(f.getName()) == f.getLastUpdate()) {
          notifyAndShutDown(f);
        } else {
          lastUpdate.put(f.getName(), f.getLastUpdate());
        }
      });
    }, timeout);

    return t;
  }

  public static void start() {
    Logger.info("Monitoring is ON");
    threads = List.of(monitorThermometers(),
        monitorOnStatus(),
        monitorCredits());
    threads.forEach(it -> new Thread(it).start());
  }

  public void stop() {
    if (threads != null) {
      threads.forEach(it -> it.stop());
    }
  }
}
