package net.toshimichi.sushi.modules.config;

public class GsonConfigurationCategory implements ConfigurationCategory {

    private final String id;
    private final String name;
    private final String description;

    public GsonConfigurationCategory(String id, String name, String description) {
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
}
