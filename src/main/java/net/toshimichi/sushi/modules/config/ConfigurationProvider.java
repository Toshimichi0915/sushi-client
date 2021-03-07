package net.toshimichi.sushi.modules.config;

import java.util.List;
import java.util.function.Supplier;

public interface ConfigurationProvider {
    <T> Configuration<T> getConfiguration(String name, Class<T> t, T defaultValue, Supplier<Boolean> isValid);

    default <T> Configuration<T> getConfiguration(String name, Class<T> t, T defaultValue) {
        return getConfiguration(name, t, defaultValue, () -> true);
    }

    List<Configuration<?>> getConfigurations();
}
