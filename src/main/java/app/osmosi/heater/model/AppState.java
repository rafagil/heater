package app.osmosi.heater.model;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.io.Serializable;

import static app.osmosi.heater.utils.JsonObjectBuilder.*;

public class AppState implements JsonObject, Serializable {
	private static final long serialVersionUID = 1L;
	private final HotWater hotWater;
	private final Set<HotWaterTimer> timers;
	private final Map<String, Floor> floorByName;
	private final Set<Floor> floors;

	public AppState(HotWater hotWater, Set<HotWaterTimer> timers, Set<Floor> floors) {
		this.hotWater = hotWater;
		this.floors = floors;
		this.floorByName = this.floors.stream()
				.collect(Collectors.toMap(Floor::getName, Function.identity()));
		this.timers = timers;
	}

	public AppState(HotWater hotWater, Set<HotWaterTimer> timers, Floor... floors) {
		this(hotWater, timers, Set.of(floors));
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

	public Set<Floor> getFloors() {
		return floors;
	}

	public String asJson() {
		return object(
				key("floors", array(floors.stream().map(Floor::asJson))),
				key("hotWater", getHotWater().asJson()),
				key("timers", array(timers.stream().map(HotWaterTimer::asJson))));
	}
}
