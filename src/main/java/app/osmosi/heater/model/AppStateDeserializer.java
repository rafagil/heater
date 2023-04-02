package app.osmosi.heater.model;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

  private HotWaterTimer parseTimer(JsonNode node) {
    int hours = node.get("hours").asInt();
    int minutes = node.get("minutes").asInt();
    int timeout = node.get("timeout").asInt();
    int dayTriggered = node.get("dayTriggered").asInt();

    return new HotWaterTimer(hours, minutes, timeout, dayTriggered);

  }

  @Override
  public AppState deserialize(JsonParser parser, DeserializationContext ctx) throws IOException, JacksonException {
    JsonNode node = parser.getCodec().readTree(parser);
    Floor cima = parseFloor(node.get("cima"));
    Floor baixo = parseFloor(node.get("baixo"));
    HotWater hotWater = parseHotWater(node.get("hotWater"));
    Set<HotWaterTimer> timers = new HashSet<>();
    node.get("timers")
        .elements()
        .forEachRemaining(t -> timers.add(parseTimer(t)));

    return new AppState(cima, baixo, hotWater, Collections.unmodifiableSet(timers));
  }

}
