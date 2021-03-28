package net.toshimichi.sushi.gui.hud;

import net.toshimichi.sushi.gui.PanelComponent;

public class HudComponent extends PanelComponent<ConfigHudElementComponent<?>> {

    public ConfigHudElementComponent<?> getHudComponent(String id) {
        for (ConfigHudElementComponent<?> component : this) {
            if (component.getId().equals(id))
                return component;
        }
        return null;
    }
}
