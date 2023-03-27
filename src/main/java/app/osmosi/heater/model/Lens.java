package app.osmosi.heater.model;

import java.util.function.BiFunction;
import java.util.function.Function;

public class Lens<A, B> {
    private final Function<A, B> getter;
    private final BiFunction<A, B, A> setter;

    private Lens(Function<A, B> getter, BiFunction<A, B, A> setter) {
        this.getter = getter;
        this.setter = setter;
    }

    public static <A, B> Lens<A, B> of(Function<A, B> getter, BiFunction<A, B, A> setter) {
        return new Lens<>(getter, setter);
    }

    public B get(A a) {
        return getter.apply(a);
    }

    public Function<A, A> modify(Function<B, B> mapper) {
        return (oldValue) -> {
            return setter.apply(oldValue, mapper.apply(getter.apply(oldValue)));
        };
    }
}
