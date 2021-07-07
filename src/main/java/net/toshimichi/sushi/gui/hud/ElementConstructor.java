package net.toshimichi.sushi.gui.hud;

import net.toshimichi.sushi.config.Configurations;

@FunctionalInterface
public interface ElementConstructor {
    BaseHudElementComponent newElement(Configurations configurations, String id, String name);
}
