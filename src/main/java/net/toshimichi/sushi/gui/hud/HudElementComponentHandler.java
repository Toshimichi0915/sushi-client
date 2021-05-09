package net.toshimichi.sushi.gui.hud;

import net.toshimichi.sushi.gui.ComponentHandler;

public interface HudElementComponentHandler extends ComponentHandler {
    default void setActive(boolean active) {
    }
}
