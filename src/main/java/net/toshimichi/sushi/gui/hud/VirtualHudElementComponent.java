package net.toshimichi.sushi.gui.hud;

import net.toshimichi.sushi.gui.base.BaseComponent;

abstract public class VirtualHudElementComponent extends BaseComponent implements HudElementComponent {

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public void setActive(boolean active) {
    }
}
