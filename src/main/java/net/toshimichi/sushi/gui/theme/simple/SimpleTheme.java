package net.toshimichi.sushi.gui.theme.simple;

import net.toshimichi.sushi.Sushi;
import net.toshimichi.sushi.gui.Component;
import net.toshimichi.sushi.gui.FrameComponent;
import net.toshimichi.sushi.gui.PanelComponent;
import net.toshimichi.sushi.gui.theme.Theme;
import net.toshimichi.sushi.gui.theme.ThemeConstants;
import net.toshimichi.sushi.modules.Module;
import net.toshimichi.sushi.config.Configurations;

public class SimpleTheme implements Theme {

    private final Configurations configurations;
    private final ThemeConstants constants;

    public SimpleTheme(Configurations configurations) {
        this.configurations = configurations;
        this.constants = new ThemeConstants(configurations);
    }

    @Override
    public String getId() {
        return "simple";
    }

    @Override
    public FrameComponent newFrame(Component component) {
        return new SimpleFrameComponent(constants, component);
    }

    @Override
    public PanelComponent newClickGui() {
        Module clickGui = Sushi.getProfile().getModules().getModule("clickgui");
        return new SimpleClickGuiComponent(clickGui, constants);
    }

}
