package net.toshimichi.sushi.modules.config;

public interface Configuration<T> {
    T getValue();

    void setValue(T value);
}
