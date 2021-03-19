package net.toshimichi.sushi.modules.config;

import java.util.List;
import java.util.function.Supplier;

public interface Configurations {
    <T> Configuration<T> get(String id, String name, Class<T> t, T defaultValue, Supplier<Boolean> isValid, String parent);

    default <T> Configuration<T> get(String id, String name, Class<T> t, T defaultValue) {
        return get(id, name, t, defaultValue, () -> true, null);
    }

    List<Configuration<?>> getAll();
}
