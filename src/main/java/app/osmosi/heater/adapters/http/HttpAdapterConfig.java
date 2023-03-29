package app.osmosi.heater.adapters.http;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.IOException;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

@JsonDeserialize(using = ConfigDeserializer.class)
public class HttpAdapterConfig {
  private final List<CentralHeatingConfig> centralHeating;
  private final RequestConfig hotWater;

  public HttpAdapterConfig(List<CentralHeatingConfig> centralHeating, RequestConfig hotWater) {
    this.centralHeating = centralHeating;
    this.hotWater = hotWater;
  }

  public List<CentralHeatingConfig> getCentralHeating() {
    return centralHeating;
  }

  public RequestConfig getHotWater() {
    return hotWater;
  }
}

class ConfigDeserializer extends StdDeserializer<HttpAdapterConfig> {

  public ConfigDeserializer() {
    this(null);
  }

  public ConfigDeserializer(Class<?> vc) {
    super(vc);
  }

  private CentralHeatingConfig parseCentralHeating(JsonNode node) {
    String floorName = node.get("floorName").asText();
    return new CentralHeatingConfig(floorName, parseRequestConfig(node.get("request")));
  }

  private RequestConfig parseRequestConfig(JsonNode node) {
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
    return new RequestConfig(onURL, offURL, method, onPayload, offPayload);
  }

  @Override
  public HttpAdapterConfig deserialize(JsonParser parser, DeserializationContext ctx)
      throws IOException, JacksonException {
    JsonNode node = parser.getCodec().readTree(parser);
    List<CentralHeatingConfig> centralHeating = new ArrayList<>();
    node.get("centralHeating")
        .elements()
        .forEachRemaining(n -> centralHeating.add(parseCentralHeating(n)));
    RequestConfig hotWater = parseRequestConfig(node.get("hotWater"));

    return new HttpAdapterConfig(Collections.unmodifiableList(centralHeating), hotWater);
  }

}
