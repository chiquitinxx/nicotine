package dev.yila.nicotine.failure;

import dev.yila.functional.failure.Failure;

import java.util.List;

public class CircularDependenciesFailure implements Failure {

    private final List<Class> classesStack;
    public CircularDependenciesFailure(List<Class> classesStack) {
        this.classesStack = classesStack;
    }

    public List<Class> getClassesStack() {
        return classesStack;
    }
}
