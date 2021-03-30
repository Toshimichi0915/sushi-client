package net.toshimichi.sushi.config;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GsonConfiguration<T> implements Configuration<T> {

    private final String id;
    private final String name;
    private final String description;
    private final Class<T> tClass;
    private final T defaultValue;
    private final GsonConfigurations provider;
    private final Supplier<Boolean> isValid;
    private final String parent;
    private final boolean temporary;
    private final ArrayList<Consumer<T>> handlers = new ArrayList<>();

    public GsonConfiguration(String id, String name, String description, Class<T> tClass, T defaultValue, GsonConfigurations provider, Supplier<Boolean> isValid, String parent, boolean temporary) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.tClass = tClass;
        this.defaultValue = defaultValue;
        this.provider = provider;
        this.isValid = isValid;
        this.parent = parent;
        this.temporary = temporary;
    }

    @Override
    public T getValue() {
        if (temporary)
            return defaultValue;
        else
            return provider.getRawValue(id, tClass);
    }

    @Override
    public void setValue(T value) {
        if (temporary)
            throw new IllegalStateException("Could not set value to temporary configuration: " + id);
        handlers.forEach(c -> c.accept(value));
        provider.setRawValue(id, value);
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
    public String getCategory() {
        return parent;
    }

    @Override
    public boolean isTemporary() {
        return temporary;
    }

    @Override
    public T getDefaultValue() {
        return defaultValue;
    }

    @Override
    public void addHandler(Consumer<T> handler) {
        handlers.add(handler);
    }

    @Override
    public void removeHandler(Consumer<T> handler) {
        handlers.remove(handler);
    }
}
