package net.toshimichi.sushi.modules.config;

public interface Configuration<T> {
    T getValue();

    void setValue(T value);

    String getName();

    Class<T> getValueClass();

    boolean isValid();
}
