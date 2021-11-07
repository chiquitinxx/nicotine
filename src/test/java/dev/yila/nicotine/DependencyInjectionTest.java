package dev.yila.nicotine;

import dev.yila.nicotine.exception.CircularDependenciesException;
import dev.yila.nicotine.exception.ServiceNotFoundException;
import dev.yila.nicotine.storage.MemoryStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DependencyInjectionTest implements ObjectsLoader, ObjectsProvider {

    @Test
    public void errorIfNotLoaded() {
        assertEquals(0, MemoryStorage.getInstance().size());
        assertThrows(ServiceNotFoundException.class, () -> getObject(InitialInterface.class));
    }

    @Test
    public void loadTheImplementationOfAnInterface() {
        loadObject(InitialInterface.class, provider -> new InitialImplementation());
        InitialInterface object = getObject(InitialInterface.class);
        assertTrue(object instanceof InitialImplementation);
        InitialInterface another = getObject(InitialInterface.class);
        assertNotSame(object, another);
    }

    @Test
    public void singletonImplementation() {
        loadObject(InitialInterface.class, provider -> new SingletonImplementation());
        InitialInterface first = getObject(InitialInterface.class);
        InitialInterface second = getObject(InitialInterface.class);
        assertSame(first, second);
        assertEquals(1, MemoryStorage.getInstance().size());
    }

    @Test
    public void dependsInitialCreation() {
        loadObject(InitialInterface.class, provider -> new InitialImplementation());
        loadObject(DependsOnInitial.class, provider -> new DependsOnInitial(provider.getObject(InitialInterface.class)));
        DependsOnInitial dependsOnInitial = getObject(DependsOnInitial.class);
        assertNotNull(dependsOnInitial.getInitialInterface());
    }

    @Test
    public void circularDependency() {
        loadObject(CircularFirst.class, provider -> new CircularFirst(provider.getObject(CircularSecond.class)));
        loadObject(CircularSecond.class, provider -> new CircularSecond(provider.getObject(CircularFirst.class)));
        assertThrows(CircularDependenciesException.class, () -> getObject(CircularFirst.class));
        assertThrows(CircularDependenciesException.class, () -> getObject(CircularSecond.class));
    }

    @BeforeEach
    public void init() {
        MemoryStorage.getInstance().clear();
    }
}
