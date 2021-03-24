package net.toshimichi.sushi.gui.base;

import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.gui.ConfigComponent;

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
