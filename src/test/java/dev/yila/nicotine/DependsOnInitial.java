package dev.yila.nicotine;

public class DependsOnInitial {
    private final InitialInterface initialInterface;

    public DependsOnInitial(InitialInterface initialInterface) {
        this.initialInterface = initialInterface;
    }

    public InitialInterface getInitialInterface() {
        return initialInterface;
    }
}
