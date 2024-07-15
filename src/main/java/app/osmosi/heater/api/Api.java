package app.osmosi.heater.api;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import app.osmosi.heater.adapters.Adapter;
import app.osmosi.heater.adapters.http.HttpAdapter;
import app.osmosi.heater.model.AppState;
import app.osmosi.heater.model.Floor;
import app.osmosi.heater.model.HotWater;
import app.osmosi.heater.model.HotWaterTimer;
import app.osmosi.heater.model.Switch;
import app.osmosi.heater.model.ZoneConfig;
import app.osmosi.heater.store.TemperatureStatePersister;
import app.osmosi.heater.store.Store;
import app.osmosi.heater.store.actions.FloorUpdateAction;
import app.osmosi.heater.store.actions.HotWaterUpdateAction;
import app.osmosi.heater.store.actions.SingleTimerUpdateAction;
import app.osmosi.heater.store.actions.TimerUpdateAction;
import app.osmosi.heater.store.reducers.AppReducer;
import app.osmosi.heater.utils.Env;
import app.osmosi.heater.utils.Logger;

public class Api {
	private static Store<AppState> store;
	private static List<Adapter> adapters;
	private static final String ZONE_CONFIG_PATH = Env.CONFIG_PATH + "/zones.org";

	private Api() {
	}

	public static void init(Store<AppState> store, List<Adapter> adapters) {
		Api.store = store;
		Api.adapters = adapters;
		// Adapters:
		adapters.forEach(a -> a.addSubscribers(store));
	}

	private static Floor[] loadZones(Map<String, Double> persistedTemps) throws IOException {
		List<ZoneConfig> configs = ZoneConfigParser.parse(new File(ZONE_CONFIG_PATH));

		Function<ZoneConfig, Floor> toFloor = config -> {
			Double temp = persistedTemps.get(config.getZone());
			temp = temp == null ? 99 : temp;
			return new Floor(config.getZone(), 0, 0, temp, Switch.OFF, 0, Set.of(config.getDevice()));
		};

		BinaryOperator<Floor> merge = (f1, f2) -> {
			if (f1 == null) {
				return f2;
			}

			return f1.withActiveDevices(
					Stream.concat(
							f1.getActiveDevices().stream(),
							f2.getActiveDevices().stream())
							.collect(Collectors.toSet()));
		};

		return configs.stream()
				.filter(ZoneConfig::isEnabled)
				.map(toFloor)
				.collect(Collectors.groupingBy(
						Floor::getName,
						Collectors.reducing(null, merge)))
				.values()
				.stream()
				.toArray(Floor[]::new);
	}

	public static void init() throws IOException {
		Floor[] zones = loadZones(TemperatureStatePersister.load());

		store = new Store<AppState>(new AppState(new HotWater(Switch.OFF), Set.of(), zones), new AppReducer());

		// Adapters:
		try {
			HttpAdapter httpAdapter = new HttpAdapter(
					new File(Env.CONFIG_PATH + "/http-adapter-ch.org"),
					new File(Env.CONFIG_PATH + "/http-adapter-hw.org"));
			adapters = List.of(httpAdapter); // Multiple adapters can be combined here
			adapters.forEach(a -> a.addSubscribers(store));
		} catch (IOException e) {
			e.printStackTrace();
			Logger.error("Failed to load the adapters. Can't contiunue.");
			System.exit(1);
		}

		// Persist the temperatures
		store.subscribe(s -> s, TemperatureStatePersister::persist);
	}

	private static Floor updateHeaterState(final Floor f) {
		final double triggerTemp = f.getHeaterState() == Switch.ON ? f.getDesiredTemp()
				: f.getDesiredTemp() - 0.5;
		if (f.getActualTemp() < triggerTemp) {
			return f.withHeaterState(Switch.ON);// .setNextHeaterSchedule (ver codigo no clojure)
		}
		return f.withHeaterState(Switch.OFF);
	}

	public static void turnOnHotWater(final int timeoutMs) {
		store.dispatch(new HotWaterUpdateAction(new HotWater(Switch.ON)));
		final long instanceId = store.getState().getHotWater().getInstanceId();
		Logger.info("Hot Water is ON");

		new Thread(() -> {
			try {
				Thread.sleep(timeoutMs);
			} catch (InterruptedException e) {
			}
			if (instanceId == store.getState().getHotWater().getInstanceId()) {
				turnOffHotWater();
			}
		}).start();
	}

	public static void turnOffHotWater() {
		store.dispatch(new HotWaterUpdateAction(new HotWater(Switch.OFF)));
		Logger.info("Hot Water is OFF");
	}

	public static void updateFloor(final Floor newFloor) {
		Floor updatedFloor = updateHeaterState(newFloor);
		store.dispatch(new FloorUpdateAction(updatedFloor));
	}

	public static void syncAdapters(boolean turnOffHotWater) {
		Logger.info("Synchronizing all adapters");
		adapters.forEach(a -> a.sync(getCurrentState()));
		if (turnOffHotWater) {
			turnOffHotWater();
		}
	}

	public static void syncAdapters() {
		syncAdapters(true);
	}

	public static void updateTimers(Set<HotWaterTimer> timers) {
		Set<HotWaterTimer> currentTimers = Api.getCurrentState().getTimers();
		Set<HotWaterTimer> existingTimers = currentTimers
				.stream()
				.filter(t -> timers.contains(t)) // Deletes Timers that are no longer in the "file"
				.collect(Collectors.toSet());
		Set<HotWaterTimer> mergedTimers = new HashSet<>(existingTimers);
		mergedTimers.addAll(timers);
		store.dispatch(new TimerUpdateAction(mergedTimers));
	}

	public static void updateTimer(HotWaterTimer timer) {
		store.dispatch(new SingleTimerUpdateAction(timer));
	}

	public static AppState getCurrentState() {
		return store.getState();
	}
}
