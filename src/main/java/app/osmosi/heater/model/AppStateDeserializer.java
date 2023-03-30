package app.osmosi.heater.model;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class AppStateDeserializer extends StdDeserializer<AppState> {

  public AppStateDeserializer() {
    this(null);
  }

  public AppStateDeserializer(Class<?> vc) {
    super(vc);
  }

  private Floor parseFloor(JsonNode node) {
    String name = node.get("name").asText();
    double desiredTemp = node.get("desiredTemp").asDouble();
    double setBackTemp = node.get("setBackTemp").asDouble();
    double actualTemp = node.get("actualTemp").asDouble();
    Switch heaterState = Switch.valueOf(node.get("heaterState").asText());
    String mqtt = node.get("mqtt").asText();
    int sonoffChannel = node.get("sonoffChannel").asInt();
    long lastUpdate = node.get("lastUpdate").asLong();

    return new Floor(name, desiredTemp, setBackTemp, actualTemp, heaterState, mqtt, sonoffChannel, lastUpdate);
  }

  private HotWater parseHotWater(JsonNode node) {
    Switch state = Switch.OFF; // Hot Water should never start "ON"
    return new HotWater(state);
  }

  @Override
  public AppState deserialize(JsonParser parser, DeserializationContext ctx) throws IOException, JacksonException {
    JsonNode node = parser.getCodec().readTree(parser);
    Floor cima = parseFloor(node.get("cima"));
    Floor baixo = parseFloor(node.get("baixo"));
    HotWater hotWater = parseHotWater(node.get("hotWater"));

    return new AppState(cima, baixo, hotWater, List.of());
  }

}
