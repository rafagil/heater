package app.osmosi.heater.store.actions;

import java.util.List;

import app.osmosi.heater.model.HotWaterTimer;

public class TimerUpdateAction implements Action {
    private final List<HotWaterTimer> data;

    public TimerUpdateAction(List<HotWaterTimer> timers) {
        this.data = timers;
    }

    public List<HotWaterTimer> getData() {
        return data;
    }
}
