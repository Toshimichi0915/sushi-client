package net.toshimichi.sushi.config;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public interface Configurations {
    <T> Configuration<T> get(String id, String name, String description, Class<T> t, T defaultValue, Supplier<Boolean> isValid, ConfigurationCategory category, boolean temporary, int priority);

    default <T> Configuration<T> get(String id, String name, String description, Class<T> t, T value) {
        return get(id, name, description, t, value, () -> true, null, false, 0);
    }

    default <T> Configuration<T> temp(String id, String name, String description, Class<T> t, T defaultValue) {
        return get(id, name, description, t, defaultValue, () -> true, null, true, 0);
    }

    List<Configuration<?>> getAll();

    ConfigurationCategory getCategory(String id, String name, String description);

    List<ConfigurationCategory> getCategories();

    default List<Configuration<?>> getByCategory(ConfigurationCategory category) {
        ArrayList<Configuration<?>> result = new ArrayList<>();
        for (Configuration<?> configuration : getAll()) {
            if ((category == null && configuration.getCategory() == null) ||
                    (category != null && category.equals(configuration.getCategory())))
                result.add(configuration);
        }
        return result;
    }
}
