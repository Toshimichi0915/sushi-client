package net.toshimichi.sushi.gui.theme.simple;

import net.toshimichi.sushi.Sushi;
import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.gui.Component;
import net.toshimichi.sushi.gui.ConfigComponent;
import net.toshimichi.sushi.gui.PanelComponent;
import net.toshimichi.sushi.gui.layout.FlowDirection;
import net.toshimichi.sushi.gui.layout.FlowLayout;
import net.toshimichi.sushi.gui.theme.Theme;
import net.toshimichi.sushi.gui.theme.ThemeConstants;
import net.toshimichi.sushi.modules.Module;
import net.toshimichi.sushi.modules.Modules;

import java.util.Arrays;

public class SimpleModuleConfigComponent extends PanelComponent<Component> {

    public SimpleModuleConfigComponent(ThemeConstants constants, Theme theme, Module module) {
        setLayout(new FlowLayout(this, FlowDirection.DOWN));
        for (Configuration<?> conf : module.getConfigurations().getAll()) {
            if (!conf.isValid()) continue;
            ConfigComponent<?> component = theme.newConfigComponent(conf);
            if (component == null) continue;
            add(component);
        }
        Modules modules = Sushi.getProfile().getModules();
        add(new SipmleClickComponent(constants, "Clone this module", () -> {
            String[] split = module.getId().split("_");
            String id = String.join("_", Arrays.copyOfRange(split, 0, split.length - 1));
            int counter = 0;
            String newId;
            do {
                newId = id + "_" + (counter++);
            } while (modules.getModule(newId) != null);
            modules.cloneModule(module.getId(), newId);
        }));
        add(new SipmleClickComponent(constants, "Remove this module", () -> modules.removeModule(module.getId())));
        setHeight(16);
    }
}
