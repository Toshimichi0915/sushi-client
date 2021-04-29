package net.toshimichi.sushi.gui.theme.simple;

import net.toshimichi.sushi.Sushi;
import net.toshimichi.sushi.gui.CollapseMode;
import net.toshimichi.sushi.gui.Insets;
import net.toshimichi.sushi.gui.SmoothCollapseComponent;
import net.toshimichi.sushi.gui.base.BasePanelComponent;
import net.toshimichi.sushi.gui.layout.FlowDirection;
import net.toshimichi.sushi.gui.layout.FlowLayout;
import net.toshimichi.sushi.gui.theme.Theme;
import net.toshimichi.sushi.gui.theme.ThemeConstants;
import net.toshimichi.sushi.modules.Category;
import net.toshimichi.sushi.modules.Module;

import java.util.ArrayList;

public class SimpleModuleListComponent extends BasePanelComponent<SimpleModuleComponent> {

    private final ThemeConstants constants;
    private final Theme theme;
    private final Category category;

    public SimpleModuleListComponent(ThemeConstants constants, Theme theme, Category category) {
        this.constants = constants;
        this.theme = theme;
        this.category = category;
        setLayout(new FlowLayout(this, FlowDirection.DOWN));
        onRelocate();
    }

    @Override
    public void onRelocate() {
        addModule:
        for (Module module : Sushi.getProfile().getModules().getModules(category)) {
            for (SimpleModuleComponent component : this) {
                if (component.getModule().equals(module)) continue addModule;
            }
            SmoothCollapseComponent<SimpleModuleConfigComponent> configComponent
                    = new SmoothCollapseComponent<>(new SimpleModuleConfigComponent(constants, theme, module), CollapseMode.DOWN, 0.1);
            SimpleModuleToggleComponent component = new SimpleModuleToggleComponent(constants, module, configComponent);
            add(new SimpleModuleComponent(module, component, configComponent), true);
        }

        removeModule:
        for (SimpleModuleComponent component : new ArrayList<>(this)) {
            for (Module module : Sushi.getProfile().getModules().getModules(category)) {
                if (component.getModule().equals(module)) continue removeModule;
            }
            remove(component);
        }
        if (isEmpty()) setMargin(new Insets(0, 0, 0, 0));
        else setMargin(new Insets(0, 2, 2, 2));

        super.onRelocate();
    }

    public Category getCategory() {
        return category;
    }
}
