package net.sushiclient.client.gui.base;

import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.gui.ConfigComponent;

public class BaseConfigComponent<T> extends BaseSettingComponent<Configuration<T>> implements ConfigComponent<T> {

    private final Configuration<T> configuration;

    public BaseConfigComponent(Configuration<T> configuration) {
        this.configuration = configuration;
    }

    @Override
    public Configuration<T> getValue() {
        return configuration;
    }
}
