package net.sushiclient.client.config;

import java.util.List;

public interface RootConfigurations extends Configurations {

    ConfigurationCategory getCategory(String id, String name, String description);

    List<ConfigurationCategory> getCategories();

    List<Configuration<?>> getAll(boolean includeCategorized);
}
