package net.toshimichi.sushi.gui.hud;

import net.toshimichi.sushi.utils.render.GuiUtils;

public class HotbarHudElementComponent extends VirtualHudElementComponent {
    @Override
    public String getId() {
        return "hotbar";
    }

    @Override
    public String getName() {
        return "Hotbar";
    }

    @Override
    public void onRelocate() {
        setWindowX(GuiUtils.getWidth() / 2 - 91);
        setWindowY(GuiUtils.getHeight() - 22);
        setWidth(182);
        setHeight(22);
    }
}
