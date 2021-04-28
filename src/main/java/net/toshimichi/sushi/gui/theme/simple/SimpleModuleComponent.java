package net.toshimichi.sushi.gui.theme.simple;

import net.toshimichi.sushi.gui.CollapseComponent;
import net.toshimichi.sushi.gui.CollapseMode;
import net.toshimichi.sushi.gui.Component;
import net.toshimichi.sushi.gui.base.BasePanelComponent;
import net.toshimichi.sushi.gui.layout.FlowDirection;
import net.toshimichi.sushi.gui.layout.FlowLayout;
import net.toshimichi.sushi.gui.theme.Theme;
import net.toshimichi.sushi.gui.theme.ThemeConstants;
import net.toshimichi.sushi.modules.Module;

public class SimpleModuleComponent extends BasePanelComponent<Component> {

    private static final double COLLAPSE_SPEED = 0.1;

    private final Module module;
    private final CollapseComponent<SimpleModuleConfigComponent> configComponent;
    private boolean collapsed;

    public SimpleModuleComponent(ThemeConstants constants, Theme theme, Module module) {
        this.module = module;
        setLayout(new FlowLayout(this, FlowDirection.DOWN));
        configComponent = new CollapseComponent<>(new SimpleModuleConfigComponent(constants, theme, module), CollapseMode.DOWN);
        add(new SimpleModuleToggleComponent(constants, module, this, configComponent), true);
        add(configComponent, true);
    }

    public boolean isCollapsed() {
        return collapsed;
    }

    public void setCollapsed(boolean collapse) {
        this.collapsed = collapse;
    }

    @Override
    public void onRelocate() {
        double progress;
        if (collapsed) progress = configComponent.getProgress() + COLLAPSE_SPEED;
        else progress = configComponent.getProgress() - COLLAPSE_SPEED;
        configComponent.setProgress(progress);
        super.onRelocate();
    }

    public Module getModule() {
        return module;
    }
}
