package net.sushiclient.client.config;

public interface RootConfigurationsHandler extends ConfigurationsHandler {
    default void getCategory(ConfigurationCategory category) {
    }
}
