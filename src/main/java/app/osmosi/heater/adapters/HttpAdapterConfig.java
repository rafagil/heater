package app.osmosi.heater.adapters;

import java.io.IOException;
import java.util.Optional;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

@JsonDeserialize(using = ConfigDeserializer.class)
public class HttpAdapterConfig {
  private final String floorName;
  private final String onURL;
  private final String offURL;
  private final String method;
  private final String onPayload;
  private final String offPayload;

  public HttpAdapterConfig(String floorName, String onURL, String offURL, String method, String onPayload,
      String offPayload) {
    this.floorName = floorName;
    this.onURL = onURL;
    this.offURL = offURL;
    this.method = method;
    this.onPayload = onPayload;
    this.offPayload = offPayload;
  }

  public String getFloorName() {
    return floorName;
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

class ConfigDeserializer extends StdDeserializer<HttpAdapterConfig> {

  public ConfigDeserializer() {
    this(null);
  }

  public ConfigDeserializer(Class<?> vc) {
    super(vc);
  }

  @Override
  public HttpAdapterConfig deserialize(JsonParser parser, DeserializationContext ctx)
      throws IOException, JacksonException {
    JsonNode node = parser.getCodec().readTree(parser);
    String floorName = node.get("floorName").asText();
    String onURL = node.get("onURL").asText();
    String offURL = node.get("offURL").asText();
    String method = node.get("method").asText();
    String onPayload = "";
    String offPayload = "";
    JsonNode onPayloadNode = node.get("onPayload");
    JsonNode offPayloadNode = node.get("offPayload");
    if (onPayloadNode != null) {
      onPayload = onPayloadNode.asText();
    }
    if (offPayloadNode != null) {
      offPayload = offPayloadNode.asText();
    }

    return new HttpAdapterConfig(floorName, onURL, offURL, method, onPayload, offPayload);
  }

}
