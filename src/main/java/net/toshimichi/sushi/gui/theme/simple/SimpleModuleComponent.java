package net.toshimichi.sushi.gui.theme.simple;

import net.toshimichi.sushi.gui.base.BaseComponent;
import net.toshimichi.sushi.modules.Module;
import net.toshimichi.sushi.modules.config.Configuration;
import net.toshimichi.sushi.modules.config.Configurations;
import net.toshimichi.sushi.utils.GuiUtils;

import java.awt.Color;

public class SimpleModuleComponent extends BaseComponent {

    private final Module module;
    private final Configuration<Color> normalColor;

    public SimpleModuleComponent(Module module, Configurations configurations) {
        this.module = module;
        this.normalColor = configurations.get("gui.module.normal_color", "Normal Color", Color.class, new Color(255, 255, 255));
        setHeight(20);
    }

    @Override
    public void onRender() {
        GuiUtils.drawRect(getWindowX(), getWindowY(), getWidth(), getHeight(), normalColor.getValue());
        GuiUtils.drawText(getWindowX() + 10, getWindowY() + 10, module.getName(), null, new Color(255, 0, 0), 9, false);
    }

    public Module getModule() {
        return module;
    }
}
