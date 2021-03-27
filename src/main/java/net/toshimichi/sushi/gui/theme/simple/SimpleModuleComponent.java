package net.toshimichi.sushi.gui.theme.simple;

import net.toshimichi.sushi.gui.CollapseComponent;
import net.toshimichi.sushi.gui.CollapseMode;
import net.toshimichi.sushi.gui.Component;
import net.toshimichi.sushi.gui.PanelComponent;
import net.toshimichi.sushi.gui.layout.FlowDirection;
import net.toshimichi.sushi.gui.layout.FlowLayout;
import net.toshimichi.sushi.gui.theme.Theme;
import net.toshimichi.sushi.gui.theme.ThemeConstants;
import net.toshimichi.sushi.modules.Module;

public class SimpleModuleComponent extends PanelComponent<Component> {

    private static final double COLLAPSE_SPEED = 0.1;

    private final Module module;
    private final CollapseComponent configComponent;
    private boolean collapsed;

    public SimpleModuleComponent(ThemeConstants constants, Theme theme, Module module) {
        this.module = module;
        setLayout(new FlowLayout(this, FlowDirection.DOWN));
        configComponent = new CollapseComponent(new SimpleModuleConfigComponent(constants, theme, module), CollapseMode.DOWN);
        add(new SimpleModuleToggleComponent(constants, module, this, configComponent));
        add(configComponent);
    }

    public boolean isCollapsed() {
        return collapsed;
    }

    public void setCollapsed(boolean collapse) {
        this.collapsed = collapse;
    }

    @Override
    public void onRender() {
        double progress;
        if (collapsed) progress = configComponent.getProgress() + COLLAPSE_SPEED;
        else progress = configComponent.getProgress() - COLLAPSE_SPEED;
        configComponent.setProgress(progress);
        super.onRender();
    }

    public Module getModule() {
        return module;
    }
}
