package app.osmosi.heater.store;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import app.osmosi.heater.store.actions.Action;
import app.osmosi.heater.store.reducers.Reducer;

public class Store<T> {
    T currentState;
    final Reducer<T> reducer;
    final List<StoreConsumer> consumers = new ArrayList<>();

    class StoreConsumer {
        private Consumer<T> consumer;
        private BiFunction<T, T, Boolean> compareFn;
    }

    public Store(T initialState, Reducer<T> reducer) {
        this.currentState = initialState;
        this.reducer = reducer;
    }

    public T getState() {
        return currentState;
    }

    public void dispatch(Action action) {
        T newState = reduce(action);
        notifySubscribers(currentState, newState);
        this.currentState = newState;
    }

    public void subscribe(BiFunction<T, T, Boolean> compare, Consumer<T> subscriber) {
        StoreConsumer sc = new StoreConsumer();
        sc.compareFn = compare;
        sc.consumer = subscriber;
        consumers.add(sc);
    }

    public void subscribe(Function<T, Object> compareField, Consumer<T> subscriber) {
        subscribe((o, n) -> compareField.apply(o) != compareField.apply(n), subscriber);
    }

    private void notifySubscribers(T oldState, T newState) {
        consumers.forEach(sc -> {
            if (sc.compareFn.apply(oldState, newState)) {
                sc.consumer.accept(newState);
            }
        });
    }

    private T reduce(Action action) {
        return reducer.reduce(action, this.currentState);
    }
}
