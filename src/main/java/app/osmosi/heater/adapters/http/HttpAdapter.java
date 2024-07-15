package app.osmosi.heater.adapters.http;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import app.osmosi.heater.adapters.Adapter;
import app.osmosi.heater.model.AppState;
import app.osmosi.heater.model.Device;
import app.osmosi.heater.model.Floor;
import app.osmosi.heater.model.Switch;
import app.osmosi.heater.store.Store;
import app.osmosi.heater.utils.Env;
import app.osmosi.heater.utils.Logger;
import app.osmosi.heater.utils.MobileNotification;

public class HttpAdapter implements Adapter {
	private HttpAdapterConfig config;
	private List<CentralHeatingConfig> centralHeatingConfigs;
	private Map<String, List<CentralHeatingConfig>> configsByFloorName;

	public HttpAdapter(File chConfigFile, File hwConfigFile) throws IOException {
		config = new HttpAdapterConfig(chConfigFile, hwConfigFile);
		centralHeatingConfigs = config.getCentralHeating();
		configsByFloorName = centralHeatingConfigs.stream()
				.collect(Collectors.groupingBy(CentralHeatingConfig::getFloorName));
	}

	private void handleSwitch(Switch state, RequestConfig config) {
		String urlString = state == Switch.ON ? config.getOnURL() : config.getOffURL();
		Optional<String> payload = state == Switch.ON ? config.getOnPayload() : config.getOffPayload();
		if (Env.DEBUG) {
			Logger.info(config.getMethod() + ": " + urlString);
			if (payload.isPresent()) {
				Logger.info(payload.get());
			}
		} else {
			new Thread(() -> {
				try {
					URL url = new URL(urlString);
					HttpURLConnection con = (HttpURLConnection) url.openConnection();
					con.setRequestMethod(config.getMethod());
					con.setRequestProperty("Content-Type", "application/json");

					if (config.getMethod().equals("POST") && payload.isPresent()) {
						DataOutputStream wr;
						con.setDoOutput(true);
						wr = new DataOutputStream(con.getOutputStream());
						wr.writeBytes(payload.get());
						wr.flush();
						wr.close();
						Logger.debug("Payload sent:");
						Logger.debug(payload);
					}

					Logger.debug("Response Code:");
					Logger.debug(con.getResponseCode());

					con.disconnect();
				} catch (IOException e) {
					MobileNotification.sendNotification("Unable to request the adapter: " + config.getIdentifier());
					e.printStackTrace();
				}
			}).start();
		}
	}

	private void handleFloor(Floor f) {
		Optional.ofNullable(configsByFloorName.get(f.getName()))
				.orElse(List.of())
				.stream()
				.filter(config -> f.getActiveDevices().contains(Device.valueOf(config.getDeviceName())))
				.forEach(c -> handleSwitch(f.getHeaterState(), c.getRequest()));
	}

	private void turnOffUnusedDevices(Floor f) {
		Optional.ofNullable(configsByFloorName.get(f.getName()))
				.orElse(List.of())
				.stream()
				.filter(config -> !f.getActiveDevices()
						.contains(Device.valueOf(config.getDeviceName())))
				.forEach(config -> handleSwitch(Switch.OFF, config.getRequest()));
	}

	@Override
	public void addSubscribers(Store<AppState> store) {
		Consumer<String> addSubscriber = name -> {
			if (store.getState().getFloorByName(name) != null) {
				// Subscribe to changes in the heater state (ON/OFF):
				store.subscribe(s -> s.getFloorByName(name).getHeaterState(),
						appState -> handleFloor(appState.getFloorByName(name)));

				// Subscribe to device changes to turn them off if not active:
				store.subscribe(s -> s.getFloorByName(name).getActiveDevices(),
						appState -> turnOffUnusedDevices(appState.getFloorByName(name)));
			} else {
				Logger.info("Not using Adapter for Zone " + name);
			}
		};

		// Adds Central Heating Subscribers
		configsByFloorName.keySet().forEach(addSubscriber);

		// Adds HotWater Subscriber
		store.subscribe(s -> s.getHotWater().getState(),
				s -> handleSwitch(s.getHotWater().getState(), config.getHotWater()));
	}

	@Override
	public void sync(AppState state) {
		state.getFloors().forEach(this::handleFloor);
		state.getFloors().forEach(this::turnOffUnusedDevices);
		handleSwitch(state.getHotWater().getState(), config.getHotWater());
	}
}
