package dev.yila.nicotine.storage;

import dev.yila.functional.Mutation;
import dev.yila.functional.Result;
import dev.yila.nicotine.ObjectsProvider;
import dev.yila.nicotine.Singleton;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class MemoryStorage {

    //TODO Thread-safe review
    private static final MemoryStorage INSTANCE = new MemoryStorage();

    private Map<Class<?>, Object> singletons = new ConcurrentHashMap<>();
    private Mutation<FunctionStore> functionsStore = Mutation.create(new FunctionStore());

    public static MemoryStorage getInstance() {
        return INSTANCE;
    }

    private MemoryStorage() {}

    public <T> Result<T> getService(ObjectsProvider source, Class<T> clazz) {
        return getStoredService(source, clazz);
    }

    private <T> Result<T> getStoredService(ObjectsProvider source, Class<T> clazz) {
        return getCreateServiceFunction(clazz)
                .flatMap(createServiceFunction -> getSingletonOrCreateService(source, clazz, createServiceFunction));
    }

    public <T> void load(Class<T> clazz, Function<ObjectsProvider, Result<T>> createServiceFunction) {
        this.functionsStore.mutate(store -> store.put(clazz, createServiceFunction));
        this.singletons.remove(clazz);
    }

    public int size() {
        return this.functionsStore.get().size();
    }

    public void clear() {
        this.functionsStore = Mutation.create(new FunctionStore());
        this.singletons.clear();
    }

    private <T> Result<T> getSingletonOrCreateService(ObjectsProvider source, Class<T> clazz, Function<ObjectsProvider, Result<T>> createServiceFunction) {
        Optional<T> singleton = getSingleton(clazz);
        return singleton
                .map(Result::ok)
                .orElseGet(() -> createNewService(source, clazz, createServiceFunction)
        );
    }

    private <T> Result<T> createNewService(ObjectsProvider source, Class<T> clazz, Function<ObjectsProvider, Result<T>> createServiceFunction) {
        LoaderResolver loaderResolver;
        if (source instanceof LoaderResolver) {
            loaderResolver = (LoaderResolver) source;
        } else {
            loaderResolver = new LoaderResolver();
        }
        Result<T> result = createServiceFunction.apply(loaderResolver);
        return result.onSuccess(object -> {
            if (object.getClass().isAnnotationPresent(Singleton.class)) {
                putSingleton(clazz, object);
            }
        });
    }

    private <T> Result<Function<ObjectsProvider, Result<T>>> getCreateServiceFunction(Class<T> clazz) {
        return functionsStore.get().getFunction(clazz);
    }

    private <T> Optional<T> getSingleton(Class<T> clazz) {
        return Optional.ofNullable((T)singletons.get(clazz));
    }

    private <T> void putSingleton(Class<T> clazz, T service) {
        singletons.put(clazz, service);
    }
}
