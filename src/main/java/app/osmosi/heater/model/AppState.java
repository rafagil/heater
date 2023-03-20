package app.osmosi.heater.model;

import java.util.Map;

public class AppState {
    private final Floor cima;
    private final Floor baixo;
    private final HotWater hotWater;
    private Map<String, Floor> floorByName;

    public AppState(Floor cima, Floor baixo, HotWater hotWater) {
        this.cima = cima;
        this.baixo = baixo;
        this.hotWater = hotWater;
        this.floorByName = Map.of("Cima", cima, "Baixo", baixo);
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

    public Floor getFloorByName(String name) {
        return floorByName.get(name);
    }
}
