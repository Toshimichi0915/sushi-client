package net.toshimichi.sushi.config;

public interface Configuration<T> {
    T getValue();

    void setValue(T value);

    String getId();

    String getName();

    String getDescription();

    Class<T> getValueClass();

    boolean isValid();

    String getCategory();
}
