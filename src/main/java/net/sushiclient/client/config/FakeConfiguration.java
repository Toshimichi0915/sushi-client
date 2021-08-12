package net.sushiclient.client.config;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FakeConfiguration<T> implements Configuration<T> {

    private final String id;
    private final String name;
    private final String description;
    private final Class<T> valueClass;
    private final T defaultValue;
    private final Supplier<Boolean> valid;
    private T value;
    private final ArrayList<ConfigurationHandler<T>> handlers = new ArrayList<>();
    private final ArrayList<Consumer<T>> consumers = new ArrayList<>();

    public FakeConfiguration(String id, String name, String description, Class<T> valueClass, T value, Supplier<Boolean> valid) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.valueClass = valueClass;
        this.value = value;
        this.defaultValue = value;
        this.valid = valid;
    }

    public FakeConfiguration(String id, String name, String description, Class<T> valueClass, T value) {
        this(id, name, description, valueClass, value, () -> true);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public T getValue() {
        handlers.forEach(c -> c.getValue(value));
        return value;
    }

    @Override
    public void setValue(T value) {
        this.value = value;
        handlers.forEach(c -> c.setValue(value));
        consumers.forEach(c -> c.accept(value));
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Class<T> getValueClass() {
        return valueClass;
    }

    @Override
    public boolean isValid() {
        return valid.get();
    }

    @Override
    public ConfigurationCategory getCategory() {
        return null;
    }

    @Override
    public boolean isTemporary() {
        return false;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public T getDefaultValue() {
        return defaultValue;
    }

    @Override
    public void addHandler(ConfigurationHandler<T> handler) {
        handlers.add(handler);
    }

    @Override
    public void removeHandler(ConfigurationHandler<T> handler) {
        handlers.remove(handler);
    }

    @Override
    public void addHandler(Consumer<T> handler) {
        consumers.add(handler);
    }

    @Override
    public void removeHandler(Consumer<T> handler) {
        consumers.remove(handler);
    }
}
