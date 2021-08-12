package net.sushiclient.client.gui.hud;

import net.sushiclient.client.config.Configurations;

@FunctionalInterface
public interface ElementConstructor {
    BaseHudElementComponent newElement(Configurations configurations, String id, String name);
}
