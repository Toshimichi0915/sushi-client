package net.toshimichi.sushi.gui.theme.simple;

import net.toshimichi.sushi.gui.Component;
import net.toshimichi.sushi.gui.FrameComponent;
import net.toshimichi.sushi.gui.PanelComponent;
import net.toshimichi.sushi.gui.theme.Theme;
import net.toshimichi.sushi.modules.config.Configurations;

public class SimpleTheme implements Theme {

    private final Configurations configurations;

    public SimpleTheme(Configurations configurations) {
        this.configurations = configurations;
    }

    @Override
    public String getId() {
        return "simple";
    }

    @Override
    public FrameComponent newFrame(Component component) {
        return new SimpleFrameComponent(configurations, component);
    }

    @Override
    public PanelComponent newClickGui() {
        return new SimpleClickGuiComponent(configurations);
    }
}
