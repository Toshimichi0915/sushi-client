package net.sushiclient.client.gui.theme.simple;

import net.sushiclient.client.gui.AnyPanelComponent;
import net.sushiclient.client.gui.SmoothCollapseComponent;
import net.sushiclient.client.gui.layout.FlowDirection;
import net.sushiclient.client.gui.layout.FlowLayout;
import net.sushiclient.client.modules.Module;

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
