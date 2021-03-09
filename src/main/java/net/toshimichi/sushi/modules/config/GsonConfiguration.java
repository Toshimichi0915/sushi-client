package net.toshimichi.sushi.modules.config;

import java.util.function.Supplier;

public class GsonConfiguration<T> implements Configuration<T> {

    private final String name;
    private final Class<T> tClass;
    private final GsonConfigurationProvider provider;
    private final Supplier<Boolean> isValid;
    private final Configuration<?> parent;

    public GsonConfiguration(String name, Class<T> tClass, GsonConfigurationProvider provider, Supplier<Boolean> isValid, Configuration<?> parent) {
        this.name = name;
        this.tClass = tClass;
        this.provider = provider;
        this.isValid = isValid;
        this.parent = parent;
    }

    @Override
    public T getValue() {
        return provider.getRawValue(name, tClass);
    }

    @Override
    public void setValue(T value) {
        provider.setRawValue(name, value);
    }

    @Override
    public String getName() {
        return name;
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
    public Configuration<?> getParent() {
        return parent;
    }
}
