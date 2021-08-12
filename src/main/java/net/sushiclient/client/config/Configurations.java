package net.sushiclient.client.config;

import java.util.List;
import java.util.function.Supplier;

public interface Configurations {
    <T> Configuration<T> get(String id, String name, String description, Class<T> t, T defaultValue, Supplier<Boolean> isValid, boolean temporary, int priority);

    default <T> Configuration<T> get(String id, String name, String description, Class<T> t, T value) {
        return get(id, name, description, t, value, () -> true, false, 0);
    }

    default <T> Configuration<T> temp(String id, String name, String description, Class<T> t, T defaultValue) {
        return get(id, name, description, t, defaultValue, () -> true, true, 0);
    }

    List<Configuration<?>> getAll();

    default void reset() {
        for (Configuration<?> conf : getAll()) {
            conf.reset();
        }
    }
}
