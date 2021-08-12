package net.sushiclient.client.gui;

import net.sushiclient.client.config.Configuration;

@FunctionalInterface
public interface ConfigComponentFactory<T> {
    ConfigComponent<T> newConfigComponent(Configuration<T> conf);
}
