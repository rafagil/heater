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
import app.osmosi.heater.model.Device;
import app.osmosi.heater.utils.Env;
import app.osmosi.heater.utils.FileUtils;
import app.osmosi.heater.utils.IntervalStateThread;
import app.osmosi.heater.utils.IntervalThread;
import app.osmosi.heater.utils.Logger;
import app.osmosi.heater.utils.Worker;

public class Monitor {
  private static List<Worker> threads;
  public static final String BALANCE_FILE_PATH = Env.DB_PATH + "/balance.txt";
  private static final int LOW_CREDIT_THRESHOLD = 12;
  private static final int CREDIT_NOTIF_INTERVAL = 120; // Minutes between notifications.

  private static int minutesToMs(int minutes) {
    return minutes * 60 * 1000;
  }

  private static void notifyAndShutDown(Floor f) {
    String msg = "Temperature not changing on " + f.getName();
    Logger.info(msg);
    sendNotification(msg);
    Api.updateFloor(f.withActualTemp(999));
  }

  /**
   * Returns true if any of the Switches is turned ON.
   * Also filters by the type of Device passed
   */
  private static boolean isHeaterOn(Device device) {
    AppState state = Api.getCurrentState();
    boolean floorIsOn = state.getFloors().stream()
		.filter(f -> device == null || f.getActiveDevices().contains(device))
        .anyMatch(f -> f.getHeaterState() == Switch.ON);
    return floorIsOn || state.getHotWater().getState() == Switch.ON;
  }

  private static IntervalStateThread<Integer> monitorCredits() {
    final float hourlyRate = 1; // 1 EUR per hour (approx)
    final float minuteRate = hourlyRate / 60;
    final int timeout = minutesToMs(1);
    IntervalStateThread<Integer> it = new IntervalStateThread<>((minutes) -> {
      try (Stream<String> lines = FileUtils.read(BALANCE_FILE_PATH)) {
        Optional<String> line = lines.findFirst();
        float balance = Float.valueOf(line.orElse("0"));

        if (isHeaterOn(Device.GAS)) {
          FileUtils.write(BALANCE_FILE_PATH, String.valueOf(balance - minuteRate));
        }

		if (balance < LOW_CREDIT_THRESHOLD) {
		  if (minutes == CREDIT_NOTIF_INTERVAL) {
		    sendNotification("Running out of gas credits!");
		    return 0;
	      } else {
		    return minutes + 1;
		  }
		}
      } catch (IOException e) {
        String msg = "Could not update current balance.";
        sendNotification(msg);
        Logger.error(msg);
      }
	  return CREDIT_NOTIF_INTERVAL;
    }, timeout, CREDIT_NOTIF_INTERVAL);
    return it;
  }

  /**
   * Checks for how long the heater is turned on.
   * it will send a notification when the number of 'ticks' exceeds the warning
   * limit.
   * it will shut down the heating when it exceeds the critical limit.
   */
  private static IntervalStateThread<Integer> monitorOnStatus() {
    final int timeout = minutesToMs(1);
    final int warningLimit = 120; // 2h
    final int criticalLimit = 240; // 4h

    IntervalStateThread<Integer> it = new IntervalStateThread<>((minutes) -> {
      if (minutes > criticalLimit) {
        sendNotification("Critical: Gas Heater will be shut down. Use 'Back Home' to turn it on again.");
        Api.getCurrentState().getFloors().forEach(f -> Api.updateFloor(f.withSetBackTemp(1)));
        Api.turnOffHotWater();
		return 0;
      } else if (minutes == warningLimit) {
        sendNotification("Warning: Gas Heater is been ON for " + (warningLimit / 60) + "h");
      }
	  if (isHeaterOn(Device.GAS)) {
		return minutes + 1;
	  }
	  return 0;
    }, timeout, 0);
    return it;
  }

  private static IntervalStateThread<Map<Floor, Integer>> monitorZoneStatus() {
    final int timeout = minutesToMs(1);
	final int warningLimit = 120; //2h
    final int criticalLimit = 240; //4h
    
	IntervalStateThread<Map<Floor, Integer>> it = new IntervalStateThread<>((state) -> {
      AppState appState = Api.getCurrentState();
	  appState.getFloors().forEach(f -> {
		if (state.get(f) == null) {
		  state.put(f, 0);
		}
		if (f.getHeaterState() == Switch.ON) {
		  state.put(f, state.get(f) + 1);
		} else {
		  state.put(f, 0);
		}

		if (state.get(f) > criticalLimit) {
		  sendNotification("Critical: " + f.getName() + " will be shut down. Use 'Back Home' to turn it on again.");
		  Api.updateFloor(f.withSetBackTemp(1));
		} else if (state.get(f) == warningLimit) {
		  sendNotification("Warning: " + f.getName() + " is been ON for " + (warningLimit / 60) + "h");
		}
	  });
      return state; 
	}, timeout, new HashMap<Floor, Integer>());

	return it;
  }

  /**
   * Checks when was the last time the temperature was updated
   * if it wasn't updated after the timeout, it will turn off the heating.
   */
  private static IntervalStateThread<Map<Floor, Long>> monitorThermometers() {
    int timeout = minutesToMs(30);
    IntervalStateThread<Map<Floor, Long>> t = new IntervalStateThread<>((lastUpdate) -> {
      AppState appState = Api.getCurrentState();
      appState.getFloors().forEach(f -> {
        if (lastUpdate.get(f) != null && lastUpdate.get(f) == f.getLastUpdate()) {
          notifyAndShutDown(f);
        } else {
          lastUpdate.put(f, f.getLastUpdate());
        }
      });
	  return lastUpdate;
    }, timeout, new HashMap<>());

    return t;
  }

  public static void start() {
    Logger.info("Monitoring is ON");
    threads = List.of(monitorThermometers(),
        monitorOnStatus(),
		monitorZoneStatus(),
        monitorCredits());
    threads.forEach(it -> it.start());
  }

  public void stop() {
    if (threads != null) {
      threads.forEach(it -> it.stop());
    }
  }
}
