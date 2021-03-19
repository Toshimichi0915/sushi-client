package net.toshimichi.sushi.gui.clickgui;

import net.toshimichi.sushi.gui.BaseComponent;
import net.toshimichi.sushi.modules.Module;
import net.toshimichi.sushi.modules.config.Configuration;
import net.toshimichi.sushi.modules.config.Configurations;
import net.toshimichi.sushi.utils.RenderUtils;

import java.awt.*;

public class ModuleComponent extends BaseComponent {

    private final Module module;
    private final RenderUtils utils;
    private final Configuration<Color> normalColor;
    private final Configuration<Color> hoverColor;

    public ModuleComponent(Module module, Configurations configurations, RenderUtils utils) {
        this.module = module;
        this.utils = utils;
        this.normalColor = configurations.get("normal_color", Color.class, new Color(255, 255, 255));
        this.hoverColor = configurations.get("hover_color", Color.class, new Color(100, 100, 100));
        setHeight(20);
    }

    @Override
    public void onRender() {
        utils.drawRect(getWindowX(), getWindowY(), getWidth(), getHeight(), normalColor.getValue());
        utils.drawText(getWindowX(), getWindowY(), module.getName(), null, new Color(255, 0, 0), 9, false);
    }

    public Module getModule() {
        return module;
    }
}
