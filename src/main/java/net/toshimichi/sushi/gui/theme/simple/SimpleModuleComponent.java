package net.toshimichi.sushi.gui.theme.simple;

import net.toshimichi.sushi.gui.Component;
import net.toshimichi.sushi.gui.SmoothCollapseComponent;
import net.toshimichi.sushi.gui.base.BasePanelComponent;
import net.toshimichi.sushi.gui.layout.FlowDirection;
import net.toshimichi.sushi.gui.layout.FlowLayout;
import net.toshimichi.sushi.modules.Module;

public class SimpleModuleComponent extends BasePanelComponent<Component> {
    private final Module module;
    private final SimpleModuleToggleComponent toggleComponent;
    private final SmoothCollapseComponent<SimpleModuleConfigComponent> collapseComponent;

    public SimpleModuleComponent(Module module, SimpleModuleToggleComponent toggleComponent, SmoothCollapseComponent<SimpleModuleConfigComponent> collapseComponent) {
        this.module = module;
        this.toggleComponent = toggleComponent;
        this.collapseComponent = collapseComponent;
        setLayout(new FlowLayout(this, FlowDirection.DOWN));
        add(toggleComponent);
        add(collapseComponent);
    }

    public Module getModule() {
        return module;
    }
}
