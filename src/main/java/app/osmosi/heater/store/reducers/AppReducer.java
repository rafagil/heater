package app.osmosi.heater.store.reducers;

import app.osmosi.heater.model.AppState;
import app.osmosi.heater.store.actions.Action;

public class AppReducer implements Reducer<AppState> {

    private final FloorReducer cimaReducer = new FloorReducer("Cima");
    private final FloorReducer baixoReducer = new FloorReducer("Baixo");
    private final HotWaterReducer hwReducer = new HotWaterReducer();

    @Override
    public AppState reduce(Action action, AppState state) {
        return new AppState(cimaReducer.reduce(action, state.getCima()),
                            baixoReducer.reduce(action, state.getBaixo()),
                            hwReducer.reduce(action, state.getHotWater()));
    }
}
