package dev.yila.nicotine;

import dev.yila.functional.Result;
import dev.yila.nicotine.failure.CircularDependenciesFailure;
import dev.yila.nicotine.failure.ServiceNotFoundFailure;
import dev.yila.nicotine.storage.MemoryStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DependencyInjectionTest implements ObjectsLoader, ObjectsProvider {

    @Test
    public void errorIfNotLoaded() {
        assertEquals(0, MemoryStorage.getInstance().size());
        assertTrue(() -> getObject(InitialInterface.class).hasFailure(ServiceNotFoundFailure.class));
    }

    @Test
    public void loadTheImplementationOfAnInterface() {
        loadObject(InitialInterface.class, provider -> Result.ok(new InitialImplementation()));
        InitialInterface object = getObject(InitialInterface.class).get();
        assertTrue(object instanceof InitialImplementation);
        InitialInterface another = getObject(InitialInterface.class).get();
        assertNotSame(object, another);
    }

    @Test
    public void singletonImplementation() {
        loadObject(InitialInterface.class, provider -> Result.ok(new SingletonImplementation()));
        Result<InitialInterface> first = getObject(InitialInterface.class);
        Result<InitialInterface> second = getObject(InitialInterface.class);
        assertSame(first.get(), second.get());
        assertEquals(1, MemoryStorage.getInstance().size());
    }

    @Test
    public void dependsInitialCreation() {
        loadObject(InitialInterface.class, provider -> Result.ok(new InitialImplementation()));
        loadObject(DependsOnInitial.class, provider -> provider.getObject(InitialInterface.class).map(DependsOnInitial::new));
        DependsOnInitial dependsOnInitial = getObject(DependsOnInitial.class).get();
        assertNotNull(dependsOnInitial.getInitialInterface());
    }

    @Test
    public void circularDependency() {
        loadObject(CircularFirst.class, provider -> provider.getObject(CircularSecond.class).map(CircularFirst::new));
        loadObject(CircularSecond.class, provider -> provider.getObject(CircularFirst.class).map(CircularSecond::new));
        Result first = getObject(CircularFirst.class);
        assertTrue(first.hasFailure(CircularDependenciesFailure.class));
        assertTrue(getObject(CircularSecond.class).hasFailure(CircularDependenciesFailure.class));
        CircularDependenciesFailure failure = (CircularDependenciesFailure)first.getFailures().get(0);
        assertTrue(failure.getClassesStack().contains(CircularFirst.class));
    }

    @BeforeEach
    public void init() {
        MemoryStorage.getInstance().clear();
    }
}
