package dev.yila.nicotine.storage;

import dev.yila.functional.Result;
import dev.yila.nicotine.ObjectsProvider;
import dev.yila.nicotine.failure.CircularDependenciesFailure;

import java.util.ArrayList;
import java.util.List;

import static dev.yila.nicotine.storage.MemoryStorage.getInstance;

public class LoaderResolver implements ObjectsProvider {

    List<Class> pastClasses;

    @Override
    public <T> Result<T> getObject(Class<T> clazz) {
        return addToPastClasses(clazz)
                .flatMap(c -> getInstance().getService(this, c));
    }

    private <T> Result<Class<T>> addToPastClasses(Class<T> clazz) {
        if (pastClasses == null) {
            pastClasses = new ArrayList<>();
        }
        pastClasses.add(clazz);
        return validateCircularDependencies(pastClasses, clazz);
    }

    private <T> Result<Class<T>> validateCircularDependencies(List<Class> pastClasses, Class<T> current) {
        if (pastClasses.size() > 1 && hasRepeatedSequence(pastClasses)) {
            return Result.failure(new CircularDependenciesFailure(pastClasses));
        }
        return Result.ok(current);
    }

    private boolean hasRepeatedSequence(List<Class> pastClasses) {
        boolean found = false;
        int size = 1;
        int listSize = pastClasses.size();
        while (!found && ((size * 2) <= listSize)) {
            if (elementsAreEquals(pastClasses, size)) {
                found = true;
            }
            size++;
        }
        return found;
    }

    private boolean elementsAreEquals(List<Class> pastClasses, int size) {
        int lastElement = pastClasses.size() - 1;
        for (int i = 0; i < size; i++) {
            if (!pastClasses.get(lastElement - i).equals(pastClasses.get(lastElement - size - i))) {
                return false;
            }
        }
        return true;
    }
}
