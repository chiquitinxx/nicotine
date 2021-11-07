package dev.yila.nicotine.storage;

import dev.yila.nicotine.LoaderProvider;
import dev.yila.nicotine.ObjectsProvider;
import dev.yila.nicotine.Singleton;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class MemoryStorage {

    //TODO Thread-safe review
    private static final MemoryStorage INSTANCE = new MemoryStorage();

    private Map<Class<?>, Function<ObjectsProvider, ?>> mapping = new ConcurrentHashMap<>();
    private Map<Class<?>, Object> singletons = new ConcurrentHashMap<>();

    public static MemoryStorage getInstance() {
        return INSTANCE;
    }

    private MemoryStorage() {}

    public <T> T getService(ObjectsProvider source, Class<T> clazz) {
        return getStoredService(source, clazz);
    }

    private <T> T getStoredService(ObjectsProvider source, Class<T> clazz) {
        return Optional
                .ofNullable(getCreateServiceFunction(clazz))
                .map(createServiceFunction -> getSingletonOrCreateService(source, clazz, createServiceFunction))
                .orElseThrow(() -> new RuntimeException("Not found service defined by class " + clazz.getCanonicalName()));
    }

    public <T> void load(Class<T> clazz, Function<ObjectsProvider, T> createServiceFunction) {
        this.mapping.put(clazz, createServiceFunction);
        this.singletons.remove(clazz);
    }

    public int size() {
        return this.mapping.size() + this.singletons.size();
    }

    public void clear() {
        this.mapping.clear();
        this.singletons.clear();
    }

    private <T> T getSingletonOrCreateService(ObjectsProvider source, Class<T> clazz, Function<ObjectsProvider, T> createServiceFunction) {
        Optional<T> singleton = getSingleton(clazz);
        return singleton.orElseGet(() ->
                createNewService(source, clazz, createServiceFunction)
        );
    }

    private <T> T createNewService(ObjectsProvider source, Class<T> clazz, Function<ObjectsProvider, T> createServiceFunction) {
        LoaderProvider loaderProvider;
        if (source instanceof LoaderProvider) {
            loaderProvider = (LoaderProvider) source;
        } else {
            loaderProvider = new LoaderProvider();
        }
        T result = createServiceFunction.apply(loaderProvider);
        if (result.getClass().isAnnotationPresent(Singleton.class)) {
            putSingleton(clazz, result);
        }
        return result;
    }

    private <T> Function<ObjectsProvider, T> getCreateServiceFunction(Class<T> clazz) {
        return (Function<ObjectsProvider, T>) mapping.get(clazz);
    }

    private <T> Optional<T> getSingleton(Class<T> clazz) {
        return Optional.ofNullable((T)singletons.get(clazz));
    }

    private <T> void putSingleton(Class<T> clazz, T service) {
        singletons.put(clazz, service);
    }
}
