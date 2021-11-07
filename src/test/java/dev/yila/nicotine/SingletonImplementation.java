package dev.yila.nicotine;

@Singleton
public class SingletonImplementation implements InitialInterface {
    @Override
    public int add(int first, int second) {
        return first + second;
    }
}
