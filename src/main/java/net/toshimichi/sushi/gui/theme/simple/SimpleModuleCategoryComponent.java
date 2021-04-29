package net.toshimichi.sushi.gui.theme.simple;

import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.ConfigurationCategory;
import net.toshimichi.sushi.gui.Component;
import net.toshimichi.sushi.gui.ConfigComponent;
import net.toshimichi.sushi.gui.base.BasePanelComponent;
import net.toshimichi.sushi.gui.layout.FlowDirection;
import net.toshimichi.sushi.gui.layout.FlowLayout;
import net.toshimichi.sushi.gui.theme.Theme;
import net.toshimichi.sushi.modules.Module;

public class SimpleModuleCategoryComponent extends BasePanelComponent<Component> {

    public SimpleModuleCategoryComponent(Theme theme, Module module, ConfigurationCategory category) {
        setLayout(new FlowLayout(this, FlowDirection.DOWN));
        for (Configuration<?> conf : module.getConfigurations().getByCategory(category)) {
            if (!conf.isValid()) continue;
            ConfigComponent<?> component = theme.newConfigComponent(conf);
            if (component == null) continue;
            add(component, true);
        }

        sort((a, b) -> {
            int aPriority = 1000;
            int bPriority = 1000;
            if (a instanceof ConfigComponent)
                aPriority = ((ConfigComponent<?>) a).getValue().getPriority();
            if (b instanceof ConfigComponent)
                bPriority = ((ConfigComponent<?>) b).getValue().getPriority();
            return Integer.compare(aPriority, bPriority);
        });
    }
}