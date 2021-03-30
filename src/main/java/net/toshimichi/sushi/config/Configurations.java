package net.toshimichi.sushi.config;

import java.util.List;
import java.util.function.Supplier;

public interface Configurations {
    <T> Configuration<T> get(String id, String name, String description, Class<T> t, T defaultValue, Supplier<Boolean> isValid, String category, boolean temporary);

    default <T> Configuration<T> get(String id, String name, String description, Class<T> t, T value) {
        return get(id, name, description, t, value, () -> true, null, false);
    }

    default <T> Configuration<T> temp(String id, String name, String description, Class<T> t, T defaultValue) {
        return get(id, name, description, t, defaultValue, () -> true, null, true);
    }

    List<Configuration<?>> getAll();

    ConfigurationCategory newCategory(String id, String name, String description);

    List<ConfigurationCategory> getCategories();
}
