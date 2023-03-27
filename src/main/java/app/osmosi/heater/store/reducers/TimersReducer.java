package app.osmosi.heater.store.reducers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import app.osmosi.heater.model.HotWaterTimer;
import app.osmosi.heater.store.actions.Action;
import app.osmosi.heater.store.actions.AddTimerAction;
import app.osmosi.heater.store.actions.TimerUpdateAction;

public class TimersReducer implements Reducer<List<HotWaterTimer>> {

    @Override
    public List<HotWaterTimer> reduce(Action action, List<HotWaterTimer> state) {
        if (action instanceof AddTimerAction addAction) {
            List<HotWaterTimer> newList = new ArrayList<>();
            newList.add(addAction.getData());
            return Collections.unmodifiableList(newList);
        }
        if (action instanceof TimerUpdateAction updateAction) {
            return updateAction.getData();
        }
        return state;
    }
}
