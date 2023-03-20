package app.osmosi.heater.adapters;

import app.osmosi.heater.model.AppState;
import app.osmosi.heater.store.Store;

public interface Adapter {
    void addSubscribers(Store<AppState> s);
}
