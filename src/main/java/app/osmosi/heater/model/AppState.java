package app.osmosi.heater.model;

import java.util.Map;
import java.util.Set;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = AppStateDeserializer.class)
public class AppState {
  private final Floor cima;
  private final Floor baixo;
  private final HotWater hotWater;
  private final Set<HotWaterTimer> timers;
  private final Map<String, Floor> floorByName;
  private final List<Floor> floors;

  public AppState(Floor cima, Floor baixo, HotWater hotWater, Set<HotWaterTimer> timers) {
    this.cima = cima;
    this.baixo = baixo;
    this.hotWater = hotWater;
    this.floorByName = Map.of("Cima", cima, "Baixo", baixo);
    this.floors = List.of(cima, baixo);
    this.timers = timers;
  }

  public Floor getCima() {
    return cima;
  }

  public Floor getBaixo() {
    return baixo;
  }

  public HotWater getHotWater() {
    return hotWater;
  }

  public Set<HotWaterTimer> getTimers() {
    return timers;
  }

  public Floor getFloorByName(String name) {
    return floorByName.get(name);
  }

  public List<Floor> getFloors() {
    return floors;
  }
}
