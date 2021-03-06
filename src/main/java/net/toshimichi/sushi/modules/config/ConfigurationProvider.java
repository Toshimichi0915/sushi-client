package net.toshimichi.sushi.modules.config;

public interface ConfigurationProvider {
    <T> Configuration<T> getConfiguration(String name, Class<T> t, T defaultValue);
}
