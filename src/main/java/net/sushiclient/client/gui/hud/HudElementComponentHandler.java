package net.sushiclient.client.gui.hud;

import net.sushiclient.client.gui.ComponentHandler;

public interface HudElementComponentHandler extends ComponentHandler {
    default void setActive(boolean active) {
    }
}
