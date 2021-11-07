package dev.yila.nicotine;

import static dev.yila.nicotine.storage.MemoryStorage.getInstance;

public interface ObjectsProvider {

    default <T> T getObject(Class<T> clazz) {
        return getInstance().getService(this, clazz);
    }
}
