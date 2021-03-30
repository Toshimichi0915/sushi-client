package net.toshimichi.sushi.gui.hud;

import net.toshimichi.sushi.utils.GuiUtils;

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
    public void onRender() {
        setWidth(GuiUtils.getWidth());
        setHeight(GuiUtils.getHeight());
    }
}
