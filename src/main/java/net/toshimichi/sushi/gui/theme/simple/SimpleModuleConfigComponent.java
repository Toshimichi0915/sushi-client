package net.toshimichi.sushi.gui.theme.simple;

import net.toshimichi.sushi.Sushi;
import net.toshimichi.sushi.config.ConfigurationCategory;
import net.toshimichi.sushi.gui.CollapseMode;
import net.toshimichi.sushi.gui.Component;
import net.toshimichi.sushi.gui.Insets;
import net.toshimichi.sushi.gui.SmoothCollapseComponent;
import net.toshimichi.sushi.gui.base.BasePanelComponent;
import net.toshimichi.sushi.gui.layout.FlowDirection;
import net.toshimichi.sushi.gui.layout.FlowLayout;
import net.toshimichi.sushi.gui.theme.Theme;
import net.toshimichi.sushi.gui.theme.ThemeConstants;
import net.toshimichi.sushi.modules.Module;
import net.toshimichi.sushi.modules.Modules;
import net.toshimichi.sushi.modules.client.HudModule;

import java.util.Arrays;

public class SimpleModuleConfigComponent extends BasePanelComponent<Component> {
    public SimpleModuleConfigComponent(ThemeConstants constants, Theme theme, Module module) {
        setLayout(new FlowLayout(this, FlowDirection.DOWN));
        add(new SimpleConfigCategoryComponent(theme, module.getConfigurations()));
        for (ConfigurationCategory category : module.getConfigurations().getCategories()) {
            SimpleConfigCategoryComponent categoryComponent = new SimpleConfigCategoryComponent(theme, category);
            if (module instanceof HudModule && !category.getId().equals("common")) continue;
            if (category.getId().equals("common")) {
                Modules modules = Sushi.getProfile().getModules();
                categoryComponent.add(new SimpleClickComponent(constants, "Clone this module", () -> {
                    String[] split = module.getId().split("_");
                    String id = String.join("_", Arrays.copyOfRange(split, 0, split.length - 1));
                    int counter = 0;
                    String newId;
                    do {
                        newId = id + "_" + (counter++);
                    } while (modules.getModule(newId) != null);
                    modules.cloneModule(module.getId(), newId);
                }));
                categoryComponent.add(new SimpleClickComponent(constants, "Remove this module", () -> modules.removeModule(module.getId())));
            }
            SmoothCollapseComponent<?> component = new SmoothCollapseComponent<>(categoryComponent, CollapseMode.UP, 100);
            component.setMargin(new Insets(0, 2, 0, 2));
            add(new SimpleConfigCategoryHeaderComponent(constants, category, component));
            add(component);
        }
    }
}
