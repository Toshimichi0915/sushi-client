package net.toshimichi.sushi.gui.hud;

import net.toshimichi.sushi.config.Configurations;
import net.toshimichi.sushi.gui.base.BasePanelComponent;
import net.toshimichi.sushi.gui.layout.NullLayout;
import net.toshimichi.sushi.modules.Module;
import net.toshimichi.sushi.utils.GuiUtils;

public class HudComponent extends BasePanelComponent<HudElementComponent> {

    private final Module module;

    public HudComponent(Configurations conf, Module module) {
        this.module = module;
        setLayout(new NullLayout());

        HudConstants constants = new HudConstants(conf);
        add(new HotbarHudElementComponent(), true);
        addElement(new CoordinatesComponent(constants, conf), conf);
    }

    private void addElement(HudElementComponent component, Configurations conf) {
        component.addHandler(new ConfigHandler(component, this, conf));
        add(component, true);
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
