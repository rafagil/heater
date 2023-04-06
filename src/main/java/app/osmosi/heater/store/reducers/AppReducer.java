package app.osmosi.heater.store.reducers;

import app.osmosi.heater.model.AppState;
import app.osmosi.heater.store.actions.Action;

public class AppReducer implements Reducer<AppState> {

  private final FloorsReducer floorsReducer = new FloorsReducer();
  private final HotWaterReducer hwReducer = new HotWaterReducer();
  private final TimersReducer timersReducer = new TimersReducer();

  @Override
  public AppState reduce(Action action, AppState state) {
    return new AppState(
        hwReducer.reduce(action, state.getHotWater()),
        timersReducer.reduce(action, state.getTimers()),
        floorsReducer.reduce(action, state.getFloors()));
  }
}
