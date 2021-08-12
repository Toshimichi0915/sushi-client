package net.sushiclient.client.config;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GsonConfiguration<T> implements Configuration<T> {

    private final String id;
    private final String name;
    private final String description;
    private final Class<T> tClass;
    private final T defaultValue;
    private final GsonRootConfigurations provider;
    private final Supplier<Boolean> isValid;
    private final int priority;
    private final ConfigurationCategory category;
    private final boolean temporary;
    private final ArrayList<ConfigurationHandler<T>> handlers = new ArrayList<>();
    private final ArrayList<Consumer<T>> consumers = new ArrayList<>();

    public GsonConfiguration(String id, String name, String description, Class<T> tClass, T defaultValue, GsonRootConfigurations provider, Supplier<Boolean> isValid, ConfigurationCategory category, boolean temporary, int priority) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.tClass = tClass;
        this.defaultValue = defaultValue;
        this.provider = provider;
        this.isValid = isValid;
        this.category = category;
        this.temporary = temporary;
        this.priority = priority;
    }

    @Override
    public T getValue() {
        if (temporary)
            return defaultValue;
        else {
            T value = provider.getRawValue(id, tClass);
            handlers.forEach(c -> c.getValue(value));
            return provider.getRawValue(id, tClass);
        }
    }

    @Override
    public void setValue(T value) {
        if (temporary)
            throw new IllegalStateException("Could not set value of temporary configuration: " + id);
        provider.setRawValue(id, value);
        handlers.forEach(c -> c.setValue(value));
        consumers.forEach(c -> c.accept(value));
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
    public String getDescription() {
        return description;
    }

    @Override
    public Class<T> getValueClass() {
        return tClass;
    }

    @Override
    public boolean isValid() {
        return isValid.get();
    }

    @Override
    public ConfigurationCategory getCategory() {
        return category;
    }

    @Override
    public boolean isTemporary() {
        return temporary;
    }

    @Override
    public int getPriority() {
        return priority;
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

    public void save() {
        handlers.forEach(ConfigurationHandler::save);
    }
}
