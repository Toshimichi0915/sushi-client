package net.sushiclient.client.config;

import java.util.ArrayList;
import java.util.List;

public interface RootConfigurations extends Configurations {

    ConfigurationCategory getCategory(String id, String name, String description);

    List<ConfigurationCategory> getCategories();

    default List<Configuration<?>> getAll(boolean includeCategorized) {
        if (!includeCategorized) return getAll();
        ArrayList<Configuration<?>> result = new ArrayList<>(getAll());
        for (ConfigurationCategory category : getCategories()) {
            result.addAll(category.getAll());
        }
        return result;
    }
}
