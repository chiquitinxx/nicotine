package dev.yila.nicotine;

import dev.yila.nicotine.exception.CircularDependenciesException;

import java.util.ArrayList;
import java.util.List;

import static dev.yila.nicotine.storage.MemoryStorage.getInstance;

public class LoaderProvider implements ObjectsProvider {

    List<Class> pastClasses;

    @Override
    public <T> T getObject(Class<T> clazz) {
        return getInstance().getService(this, addToPastClasses(clazz));
    }

    private <T> Class<T> addToPastClasses(Class<T> clazz) {
        if (pastClasses == null) {
            pastClasses = new ArrayList<>();
            pastClasses.add(clazz);
        } else {
            validateCircularDependenciesException(pastClasses, clazz);
            pastClasses.add(clazz);
        }
        return clazz;
    }

    private void validateCircularDependenciesException(List<Class> pastClasses, Class current) {
        if (pastClasses.contains(current)) {
            throw new CircularDependenciesException();
        }
    }
}
