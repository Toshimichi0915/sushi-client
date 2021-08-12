package net.sushiclient.client.gui.theme.simple.config;

import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.gui.ConfigComponent;
import net.sushiclient.client.gui.theme.ThemeConstants;
import net.sushiclient.client.gui.theme.simple.SimpleClickComponent;

public class SimpleRunnableComponent extends SimpleClickComponent implements ConfigComponent<Runnable> {

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
