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

    ConfigurationCategory getCategory();

    boolean isTemporary();

    int getPriority();

    T getDefaultValue();

    void addHandler(ConfigurationHandler<T> handler);

    void removeHandler(ConfigurationHandler<T> handler);

    void addHandler(Consumer<T> handler);

    void removeHandler(Consumer<T> handler);

    default void reset() {
        setValue(getDefaultValue());
    }
}
