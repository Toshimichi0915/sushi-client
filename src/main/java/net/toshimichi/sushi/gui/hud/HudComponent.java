package net.toshimichi.sushi.gui.hud;

import net.toshimichi.sushi.config.Configurations;
import net.toshimichi.sushi.gui.base.BasePanelComponent;
import net.toshimichi.sushi.utils.GuiUtils;

public class HudComponent extends BasePanelComponent<HudElementComponent> {

    public HudComponent(Configurations configurations) {
        HudConstants constants = new HudConstants(configurations);
        add(new HotbarHudElementComponent());
        add(new ConfigHudElementComponent<>(new CoordinatesComponent(constants, configurations), this, configurations));
    }

    public HudElementComponent getHudElementComponent(String id) {
        for (HudElementComponent component : this) {
            if (component.getId().equals(id))
                return component;
        }
        return null;
    }

    @Override
    public void onRender() {
        setWidth(GuiUtils.getWidth());
        setHeight(GuiUtils.getHeight());
        super.onRender();
    }

    @Override
    public boolean onKeyPressed(int keyCode, char key) {
        return false;
    }

    @Override
    public boolean onKeyReleased(int keyCode) {
        return false;
    }
}
