package app.osmosi.heater.adapters.http;

public class CentralHeatingConfig {
  private final String floorName;
  private final RequestConfig request;

  public CentralHeatingConfig(String floorName, RequestConfig request) {
    this.floorName = floorName;
    this.request = request;
  }

  public String getFloorName() {
    return floorName;
  }

  public RequestConfig getRequest() {
    return request;
  }
}
