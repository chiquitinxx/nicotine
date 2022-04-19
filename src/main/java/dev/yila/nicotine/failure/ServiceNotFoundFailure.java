package dev.yila.nicotine.failure;

import dev.yila.functional.failure.Failure;

public class ServiceNotFoundFailure implements Failure {

    private final Class clazz;

    public ServiceNotFoundFailure(Class clazz) {
        this.clazz = clazz;
    }
}
