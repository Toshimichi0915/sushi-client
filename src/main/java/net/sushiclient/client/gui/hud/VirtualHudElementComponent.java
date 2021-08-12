package net.sushiclient.client.gui.hud;

import net.sushiclient.client.gui.base.BaseComponent;

abstract public class VirtualHudElementComponent extends BaseComponent implements HudElementComponent {

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public void setActive(boolean active) {
    }
}
