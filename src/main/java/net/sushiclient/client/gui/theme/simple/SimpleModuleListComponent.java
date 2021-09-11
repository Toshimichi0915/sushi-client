package net.sushiclient.client.gui.theme.simple;

import net.sushiclient.client.Sushi;
import net.sushiclient.client.gui.CollapseMode;
import net.sushiclient.client.gui.Insets;
import net.sushiclient.client.gui.SmoothCollapseComponent;
import net.sushiclient.client.gui.base.BasePanelComponent;
import net.sushiclient.client.gui.layout.FlowDirection;
import net.sushiclient.client.gui.layout.FlowLayout;
import net.sushiclient.client.gui.theme.Theme;
import net.sushiclient.client.gui.theme.ThemeConstants;
import net.sushiclient.client.modules.Category;
import net.sushiclient.client.modules.Module;
import net.sushiclient.client.modules.ModuleHandler;
import net.sushiclient.client.modules.ModulesHandler;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class SimpleModuleListComponent extends BasePanelComponent<SimpleModuleComponent> implements ModulesHandler {

    private final ThemeConstants constants;
    private final Theme theme;
    private final Category category;
    private final HashMap<Module, ModuleHandler> handlers;
    private boolean force;

    public SimpleModuleListComponent(ThemeConstants constants, Theme theme, Category category) {
        this.constants = constants;
        this.theme = theme;
        this.category = category;
        this.handlers = new HashMap<>();
        setLayout(new FlowLayout(this, FlowDirection.DOWN));
        onRelocate();
    }

    @Override
    public void onShow() {
        for (Module module : Sushi.getProfile().getModules().getAll()) {
            addModule(module);
        }
        Sushi.getProfile().getModules().addHandler(this);
    }

    @Override
    public void onClose() {
        Sushi.getProfile().getModules().removeHandler(this);
        for (Map.Entry<Module, ModuleHandler> entry : handlers.entrySet()) {
            entry.getKey().removeHandler(entry.getValue());
        }
    }

    @Override
    public void addModule(Module module) {
        if (module.getCategory().equals(category) || force) {
            SmoothCollapseComponent<SimpleModuleConfigComponent> configComponent
                    = new SmoothCollapseComponent<>(new SimpleModuleConfigComponent(constants, theme, module), CollapseMode.DOWN, 100);
            SimpleModuleToggleComponent component = new SimpleModuleToggleComponent(constants, module, configComponent);
            add(new SimpleModuleComponent(module, component, configComponent), true);
        }
        if (force) return;
        ModuleHandler handler = new ModuleHandler() {
            @Override
            public void setCategory(Category category) {
                if (SimpleModuleListComponent.this.category.equals(category)) {
                    // refresh
                    force = true;
                    addModule(module);
                    force = false;
                } else {
                    removeIf(it -> it.getModule().equals(module));
                }
            }
        };
        handlers.put(module, handler);
        module.addHandler(handler);
    }

    @Override
    public void removeModule(Module module) {
        removeIf(it -> it.getModule().equals(module));
    }

    @Override
    public void onRelocate() {
        sort(Comparator.comparing(m -> m.getModule().getName()));
        setMargin(new Insets(0, 2, 2, 2));
        super.onRelocate();
    }

    public Category getCategory() {
        return category;
    }
}
