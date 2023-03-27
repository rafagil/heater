package app.osmosi.heater.adapters;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import app.osmosi.heater.model.AppState;
import app.osmosi.heater.model.Floor;
import app.osmosi.heater.model.Switch;
import app.osmosi.heater.store.Store;

public class HttpAdapter implements Adapter {
  private List<HttpAdapterConfig> adapterConfigs;
  private Map<String, List<HttpAdapterConfig>> configsByFloorName;

  public HttpAdapter(File configFile) throws IOException {
    ObjectMapper om = new ObjectMapper();
    adapterConfigs = Arrays.asList(om.readValue(configFile, HttpAdapterConfig[].class));
    configsByFloorName = adapterConfigs.stream()
        .collect(Collectors.groupingBy(HttpAdapterConfig::getFloorName));
  }

  private void doRequest(String urlString, String method, String payload) {
    System.out.println(method + ": " + urlString + " Payload: " + payload);
    // try {
    // URL url = new URL(urlString);
    // HttpURLConnection con = (HttpURLConnection) url.openConnection();
    // con.setRequestMethod(method);
    // con.setRequestProperty("Content-Type", "application/json");
    //
    // if (method.equals("POST")) {
    // DataOutputStream wr;
    // con.setDoOutput(true);
    // wr = new DataOutputStream(con.getOutputStream());
    //
    // wr.writeBytes(payload);
    // wr.flush();
    // wr.close();
    // }
    //
    // con.getResponseCode();
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
  }

  private void turnOn(String floorName) {
    configsByFloorName.get(floorName)
        .forEach(c -> doRequest(c.getOnURL(), c.getMethod(), c.getOnPayload()));
  }

  private void turnOff(String floorName) {
    configsByFloorName.get(floorName)
        .forEach(c -> doRequest(c.getOffURL(), c.getMethod(), c.getOffPayload()));
  }

  @Override
  public void addSubscribers(Store<AppState> store) {
    Consumer<Floor> handleFloor = f -> {
      if (f.getHeaterState() == Switch.ON) {
        turnOn(f.getName());
      } else {
        turnOff(f.getName());
      }
    };
    Consumer<String> addSubscriber = name -> {
      store.subscribe(s -> s.getFloorByName(name).getHeaterState(),
          appState -> handleFloor.accept(appState.getFloorByName(name)));
    };
    configsByFloorName.keySet().forEach(addSubscriber);
  }

  @Override
  public void sync(AppState state) {
    Consumer<Floor> syncFloor = f -> {
      if (f.getHeaterState() == Switch.ON) {
        turnOn(f.getName());
      } else {
        turnOff(f.getName());
      }
    };
    state.getFloors().forEach(syncFloor);
  }

}
