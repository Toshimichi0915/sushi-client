package net.toshimichi.sushi.gui.hud;

import net.toshimichi.sushi.gui.ComponentHandler;
import net.toshimichi.sushi.gui.base.BaseComponent;

abstract public class BaseHudElementComponent extends BaseComponent implements HudElementComponent {

    private boolean active = true;

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
        for (ComponentHandler handler : getHandlers()) {
            if (handler instanceof HudElementComponentHandler) {
                ((HudElementComponentHandler) handler).setActive(active);
            }
        }
    }
}
