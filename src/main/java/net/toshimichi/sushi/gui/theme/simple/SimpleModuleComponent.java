package net.toshimichi.sushi.gui.theme.simple;

import net.toshimichi.sushi.gui.AnyPanelComponent;
import net.toshimichi.sushi.gui.SmoothCollapseComponent;
import net.toshimichi.sushi.gui.layout.FlowDirection;
import net.toshimichi.sushi.gui.layout.FlowLayout;
import net.toshimichi.sushi.modules.Module;

public class SimpleModuleComponent extends AnyPanelComponent {
    private final Module module;

    public SimpleModuleComponent(Module module, SimpleModuleToggleComponent toggleComponent, SmoothCollapseComponent<SimpleModuleConfigComponent> collapseComponent) {
        this.module = module;
        setLayout(new FlowLayout(this, FlowDirection.DOWN));
        add(toggleComponent);
        add(collapseComponent);
    }

    public Module getModule() {
        return module;
    }
}
