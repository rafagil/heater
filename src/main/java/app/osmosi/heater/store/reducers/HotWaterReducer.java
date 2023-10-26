package app.osmosi.heater.store.reducers;

import app.osmosi.heater.model.HotWater;
import app.osmosi.heater.store.actions.Action;
import app.osmosi.heater.store.actions.HotWaterUpdateAction;

public class HotWaterReducer implements Reducer<HotWater> {

	@Override
	public HotWater reduce(Action action, HotWater state) {
		if (action instanceof HotWaterUpdateAction hwua) {
			return hwua.getData();
		}
		return state;
	}
}
