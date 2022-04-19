package dev.yila.nicotine.storage;

import dev.yila.functional.Result;
import dev.yila.nicotine.ObjectsProvider;
import dev.yila.nicotine.failure.ServiceNotFoundFailure;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class FunctionStore {
    private Map<Class<?>, FunctionWrapper> mapping;

    public FunctionStore() {
        this.mapping = new HashMap<>();
    }

    public <T> FunctionStore put(Class<T> clazz, Function<ObjectsProvider, Result<T>> createServiceFunction) {
        mapping.put(clazz, new FunctionWrapper<>(createServiceFunction));
        return this;
    }

    public <T> Result<Function<ObjectsProvider, Result<T>>> getFunction(Class<T> clazz) {
        if (this.mapping.containsKey(clazz)) {
            return Result.ok(this.mapping.get(clazz).getFunction());
        }
        return Result.failure(new ServiceNotFoundFailure(clazz));
    }

    public int size() {
        return mapping.size();
    }
}
