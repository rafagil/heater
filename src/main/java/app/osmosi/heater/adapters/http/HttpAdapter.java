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

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

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

	public HttpAdapter(File configFile) throws IOException, ParserConfigurationException, SAXException {
		config = new HttpAdapterConfig(configFile);
		centralHeatingConfigs = config.getCentralHeating();
		configsByFloorName = centralHeatingConfigs.stream()
				.collect(Collectors.groupingBy(CentralHeatingConfig::getFloorName));
	}

	private void doRequest(String urlString, String method, Optional<String> payload) {
		if (Env.DEBUG) {
			Logger.info(method + ": " + urlString);
			if (payload.isPresent()) {
				Logger.info(payload.get());
			}
		} else {
			try {
				URL url = new URL(urlString);
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				con.setRequestMethod(method);
				con.setRequestProperty("Content-Type", "application/json");

				if (method.equals("POST") && payload.isPresent()) {
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
				MobileNotification.sendNotification("Unable to request the adapter");
				e.printStackTrace();
			}
		}
	}

	private void handleSwitch(Switch state, RequestConfig config) {
		if (state == Switch.ON) {
			doRequest(config.getOnURL(), config.getMethod(), config.getOnPayload());
		} else {
			doRequest(config.getOffURL(), config.getMethod(), config.getOffPayload());
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
			// Subscribe to changes in the heater state (ON/OFF):
			store.subscribe(s -> s.getFloorByName(name).getHeaterState(),
					appState -> handleFloor(appState.getFloorByName(name)));

			// Subscribe to device changes to turn them off if not active:
			store.subscribe(s -> s.getFloorByName(name).getActiveDevices(),
					appState -> turnOffUnusedDevices(appState.getFloorByName(name)));
		};

		// Adds Central Heating Subscribers
		configsByFloorName.keySet().forEach(addSubscriber);

		// Adds HotWater Subscriber
		if (config.getHotWater().isPresent()) {
			store.subscribe(s -> s.getHotWater().getState(),
					s -> handleSwitch(s.getHotWater().getState(), config.getHotWater().get()));
		}
	}

	@Override
	public void sync(AppState state) {
		state.getFloors().forEach(this::handleFloor);
		state.getFloors().forEach(this::turnOffUnusedDevices);
		if (config.getHotWater().isPresent()) {
			handleSwitch(state.getHotWater().getState(), config.getHotWater().get());
		}
	}
}
