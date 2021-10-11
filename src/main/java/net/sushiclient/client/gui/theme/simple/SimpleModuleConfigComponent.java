package net.sushiclient.client.gui.theme.simple;

import net.sushiclient.client.Sushi;
import net.sushiclient.client.config.ConfigurationCategory;
import net.sushiclient.client.gui.AnyPanelComponent;
import net.sushiclient.client.gui.CollapseMode;
import net.sushiclient.client.gui.Insets;
import net.sushiclient.client.gui.SmoothCollapseComponent;
import net.sushiclient.client.gui.layout.FlowDirection;
import net.sushiclient.client.gui.layout.FlowLayout;
import net.sushiclient.client.gui.theme.Theme;
import net.sushiclient.client.gui.theme.ThemeConstants;
import net.sushiclient.client.modules.Module;
import net.sushiclient.client.modules.Modules;
import net.sushiclient.client.modules.client.HudModule;

import java.util.Arrays;

public class SimpleModuleConfigComponent extends AnyPanelComponent {
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
                    String id;
                    if (split.length > 1) {
                        id = String.join("_", Arrays.copyOfRange(split, 0, split.length - 1));
                    } else {
                        id = module.getId();
                    }
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
