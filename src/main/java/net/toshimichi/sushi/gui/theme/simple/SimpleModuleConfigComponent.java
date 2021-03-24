package net.toshimichi.sushi.gui.theme.simple;

import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.gui.ConfigComponent;
import net.toshimichi.sushi.gui.PanelComponent;
import net.toshimichi.sushi.gui.layout.FlowDirection;
import net.toshimichi.sushi.gui.layout.FlowLayout;
import net.toshimichi.sushi.gui.theme.Theme;
import net.toshimichi.sushi.modules.Module;

public class SimpleModuleConfigComponent extends PanelComponent<ConfigComponent<?>> {

    public SimpleModuleConfigComponent(Theme theme, Module module) {
        setLayout(new FlowLayout(this, FlowDirection.DOWN));
        for (Configuration<?> conf : module.getConfigurations().getAll()) {
            if (!conf.isValid()) continue;
            ConfigComponent<?> component = theme.newConfigComponent(conf);
            if (component == null) continue;
            add(component);
        }
    }
}
