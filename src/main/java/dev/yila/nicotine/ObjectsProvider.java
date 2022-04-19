package dev.yila.nicotine;

import dev.yila.functional.Result;

import static dev.yila.nicotine.storage.MemoryStorage.getInstance;

public interface ObjectsProvider {

    default <T> Result<T> getObject(Class<T> clazz) {
        return getInstance().getService(this, clazz);
    }
}
