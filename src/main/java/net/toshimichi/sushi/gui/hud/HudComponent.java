package net.toshimichi.sushi.gui.hud;

import net.toshimichi.sushi.config.Configurations;
import net.toshimichi.sushi.gui.base.BasePanelComponent;
import net.toshimichi.sushi.modules.Module;
import net.toshimichi.sushi.utils.GuiUtils;

public class HudComponent extends BasePanelComponent<HudElementComponent> {

    private final Module module;

    public HudComponent(Configurations configurations, Module module) {
        this.module = module;
        HudConstants constants = new HudConstants(configurations);
        add(new HotbarHudElementComponent(), true);
        add(new ConfigHudElementComponent<>(new CoordinatesComponent(constants, configurations), this, configurations), true);
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

    @Override
    public void onClose() {
        module.setEnabled(false);
    }
}
