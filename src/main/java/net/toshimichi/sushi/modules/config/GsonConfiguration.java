package net.toshimichi.sushi.modules.config;

public class GsonConfiguration<T> implements Configuration<T> {

    private final String name;
    private final Class<T> tClass;
    private final GsonConfigurationProvider provider;

    public GsonConfiguration(String name, Class<T> tClass, GsonConfigurationProvider provider) {
        this.name = name;
        this.tClass = tClass;
        this.provider = provider;
    }

    @Override
    public T getValue() {
        return provider.getRawValue(name, tClass);
    }

    @Override
    public void setValue(T value) {
        provider.setRawValue(name, value);
    }
}
