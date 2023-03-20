package app.osmosi.heater.store.actions;

import app.osmosi.heater.model.HotWater;

public class HotWaterUpdateAction implements Action {
    private final HotWater data;

    public HotWaterUpdateAction(HotWater data) {
        this.data = data;
    }

    public HotWater getData() {
        return data;
    }
}
