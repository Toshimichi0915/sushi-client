package net.sushiclient.client.gui.hud;

import net.sushiclient.client.utils.render.GuiUtils;

public class WindowHudElementComponent extends VirtualHudElementComponent {
    @Override
    public String getId() {
        return "window";
    }

    @Override
    public String getName() {
        return "Window";
    }

    @Override
    public void onRelocate() {
        setWidth(GuiUtils.getWidth());
        setHeight(GuiUtils.getHeight());
    }
}
