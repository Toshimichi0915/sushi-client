package net.toshimichi.sushi.config;

import java.util.function.Supplier;

public class GsonConfiguration<T> implements Configuration<T> {

    private final String id;
    private final String name;
    private final String description;
    private final Class<T> tClass;
    private final GsonConfigurations provider;
    private final Supplier<Boolean> isValid;
    private final String parent;

    public GsonConfiguration(String id, String name, String description, Class<T> tClass, GsonConfigurations provider, Supplier<Boolean> isValid, String parent) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.tClass = tClass;
        this.provider = provider;
        this.isValid = isValid;
        this.parent = parent;
    }

    @Override
    public T getValue() {
        return provider.getRawValue(id, tClass);
    }

    @Override
    public void setValue(T value) {
        provider.setRawValue(name, value);
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
}
