package app.osmosi.heater.adapters.http;

public class RequestConfig {
  private final String onURL;
  private final String offURL;
  private final String method;
  private final String onPayload;
  private final String offPayload;

  public RequestConfig(String onURL, String offURL, String method, String onPayload,
      String offPayload) {
    this.onURL = onURL;
    this.offURL = offURL;
    this.method = method;
    this.onPayload = onPayload;
    this.offPayload = offPayload;
  }

  public String getOnURL() {
    return onURL;
  }

  public String getOffURL() {
    return offURL;
  }

  public String getMethod() {
    return method;
  }

  public String getOnPayload() {
    return onPayload;
  }

  public String getOffPayload() {
    return offPayload;
  }

}
