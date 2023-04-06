package app.osmosi.heater.adapters.http;

import java.util.Optional;

public class RequestConfig {
  private final String onURL;
  private final String offURL;
  private final String method;
  private final Optional<String> onPayload;
  private final Optional<String> offPayload;

  public RequestConfig(String onURL, String offURL, String method, Optional<String> onPayload,
      Optional<String> offPayload) {
    this.onURL = onURL;
    this.offURL = offURL;
    this.method = method;
    this.onPayload = onPayload;
    this.offPayload = offPayload;
  }

  public RequestConfig(String onURL, String offURL, String method, String onPayload,
      String offPayload) {
    this(onURL, offURL, method, Optional.ofNullable(onPayload), Optional.ofNullable(offPayload));
  }

  public RequestConfig(String onURL, String offURL, String method) {
    this.onURL = onURL;
    this.offURL = offURL;
    this.method = method;
    this.onPayload = Optional.empty();
    this.offPayload = Optional.empty();
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

  public Optional<String> getOnPayload() {
    return onPayload;
  }

  public Optional<String> getOffPayload() {
    return offPayload;
  }

}
