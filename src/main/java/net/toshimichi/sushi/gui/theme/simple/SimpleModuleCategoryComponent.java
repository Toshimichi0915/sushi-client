package net.toshimichi.sushi.gui.theme.simple;

import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.Configurations;
import net.toshimichi.sushi.gui.Component;
import net.toshimichi.sushi.gui.ConfigComponent;
import net.toshimichi.sushi.gui.base.BasePanelComponent;
import net.toshimichi.sushi.gui.layout.FlowDirection;
import net.toshimichi.sushi.gui.layout.FlowLayout;
import net.toshimichi.sushi.gui.theme.Theme;
import net.toshimichi.sushi.modules.Module;

import java.util.ArrayList;

public class SimpleModuleCategoryComponent extends BasePanelComponent<Component> {

    private final Theme theme;
    private final Module module;
    private final Configurations configurations;

    public SimpleModuleCategoryComponent(Theme theme, Module module, Configurations configurations) {
        this.theme = theme;
        this.module = module;
        this.configurations = configurations;
        setLayout(new FlowLayout(this, FlowDirection.DOWN));

    }

    private boolean contains(Configuration<?> conf) {
        for (Component component : this) {
            if (!(component instanceof ConfigComponent)) continue;
            if (((ConfigComponent<?>) component).getValue().equals(conf)) return true;
        }
        return false;
    }

    @Override
    public void onRelocate() {
        for (Configuration<?> conf : configurations.getAll()) {
            if (!conf.isValid()) continue;
            if (contains(conf)) continue;
            ConfigComponent<?> component = theme.newConfigComponent(conf);
            if (component == null) continue;
            add(component, true);
        }
        for (Component component : new ArrayList<>(this)) {
            if (!(component instanceof ConfigComponent)) continue;
            if (((ConfigComponent<?>) component).getValue().isValid()) continue;
            remove(component);
        }

        sort((a, b) -> {
            int aPriority = 1000000;
            int bPriority = 1000000;
            if (a instanceof ConfigComponent)
                aPriority = ((ConfigComponent<?>) a).getValue().getPriority();
            if (b instanceof ConfigComponent)
                bPriority = ((ConfigComponent<?>) b).getValue().getPriority();
            return Integer.compare(aPriority, bPriority);
        });

        super.onRelocate();
    }
}