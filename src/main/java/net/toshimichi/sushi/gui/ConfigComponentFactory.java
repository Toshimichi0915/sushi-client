package net.toshimichi.sushi.gui;

import net.toshimichi.sushi.config.Configuration;

@FunctionalInterface
public interface ConfigComponentFactory<T> {
    ConfigComponent<T> newConfigComponent(Configuration<T> conf);
}
