package net.toshimichi.sushi.gui.theme.simple.config;

import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.gui.ConfigComponent;
import net.toshimichi.sushi.gui.theme.ThemeConstants;
import net.toshimichi.sushi.gui.theme.simple.SipmleClickComponent;

public class SimpleRunnableComponent extends SipmleClickComponent implements ConfigComponent<Runnable> {

    private final Configuration<Runnable> configuration;

    public SimpleRunnableComponent(ThemeConstants constants, Configuration<Runnable> configuration) {
        super(constants, configuration.getName(), configuration.getValue());
        this.configuration = configuration;
    }

    @Override
    public Configuration<Runnable> getValue() {
        return configuration;
    }
}
