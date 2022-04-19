package dev.yila.nicotine.storage;

import dev.yila.functional.Result;
import dev.yila.nicotine.ObjectsProvider;

import java.util.function.Function;

public class FunctionWrapper<T> {
    private final Function<ObjectsProvider, Result<T>> function;

    public FunctionWrapper(Function<ObjectsProvider, Result<T>> function) {
        this.function = function;
    }

    public Function<ObjectsProvider, Result<T>> getFunction() {
        return function;
    }
}
