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

import com.fasterxml.jackson.databind.ObjectMapper;

import app.osmosi.heater.adapters.Adapter;
import app.osmosi.heater.model.AppState;
import app.osmosi.heater.model.Floor;
import app.osmosi.heater.model.Switch;
import app.osmosi.heater.store.Store;
import app.osmosi.heater.utils.Env;

public class HttpAdapter implements Adapter {
  private HttpAdapterConfig config;
  private List<CentralHeatingConfig> centralHeatingConfigs;
  private Map<String, List<CentralHeatingConfig>> configsByFloorName;

  public HttpAdapter(File configFile) throws IOException {
    ObjectMapper om = new ObjectMapper();
    config = om.readValue(configFile, HttpAdapterConfig.class);
    centralHeatingConfigs = config.getCentralHeating();
    configsByFloorName = centralHeatingConfigs.stream()
        .collect(Collectors.groupingBy(CentralHeatingConfig::getFloorName));
  }

  private void doRequest(String urlString, String method, String payload) {
    if (Env.DEBUG) {
      System.out.println(method + ": " + urlString);
      if (payload != null) {
        System.out.println(payload);
      }
    }
    try {
      URL url = new URL(urlString);
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      con.setRequestMethod(method);
      con.setRequestProperty("Content-Type", "application/json");

      if (method.equals("POST")) {
        DataOutputStream wr;
        con.setDoOutput(true);
        wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(payload);
        wr.flush();
        wr.close();
      }

      con.disconnect();
    } catch (IOException e) {
      e.printStackTrace();
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
        .forEach(c -> handleSwitch(f.getHeaterState(), c.getRequest()));
  }

  @Override
  public void addSubscribers(Store<AppState> store) {
    Consumer<String> addSubscriber = name -> {
      store.subscribe(s -> s.getFloorByName(name).getHeaterState(),
          appState -> handleFloor(appState.getFloorByName(name)));
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
  }

}
