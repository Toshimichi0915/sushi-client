package net.sushiclient.client.config;

public class GsonConfigurationCategory extends GsonConfigurations implements ConfigurationCategory {

    private final GsonRootConfigurations root;
    private final String id;
    private final String name;
    private final String description;

    public GsonConfigurationCategory(GsonRootConfigurations root, String id, String name, String description) {
        this.root = root;
        this.id = id;
        this.name = name;
        this.description = description;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    protected ConfigurationCategory getConfigurationCategory() {
        return this;
    }

    @Override
    protected GsonRootConfigurations getRoot() {
        return root;
    }
}
