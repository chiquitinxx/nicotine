package dev.yila.nicotine;

import dev.yila.functional.Result;

import java.util.function.Function;

import static dev.yila.nicotine.storage.MemoryStorage.getInstance;

public interface ObjectsLoader {

    default <T> ObjectsLoader loadObject(Class<T> clazz, Function<ObjectsProvider, Result<T>> function) {
        getInstance().load(clazz, function);
        return this;
    }
}
