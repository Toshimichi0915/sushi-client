package net.toshimichi.sushi.config;

import java.util.function.Consumer;

public interface Configuration<T> {
    T getValue();

    void setValue(T value);

    String getId();

    String getName();

    String getDescription();

    Class<T> getValueClass();

    boolean isValid();

    String getCategory();

    void addHandler(Consumer<T> handler);
}
