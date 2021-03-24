package net.toshimichi.sushi.gui.theme.simple;

import net.toshimichi.sushi.Sushi;
import net.toshimichi.sushi.gui.PanelComponent;
import net.toshimichi.sushi.gui.layout.FlowDirection;
import net.toshimichi.sushi.gui.layout.FlowLayout;
import net.toshimichi.sushi.gui.theme.ThemeConstants;
import net.toshimichi.sushi.modules.Category;
import net.toshimichi.sushi.modules.Module;

public class SimpleModuleListComponent extends PanelComponent<SimpleModuleComponent> {

    private final ThemeConstants constants;
    private final Category category;

    public SimpleModuleListComponent(ThemeConstants constants, Category category) {
        this.constants = constants;
        this.category = category;
        setLayout(new FlowLayout(this, FlowDirection.DOWN));
    }

    @Override
    public void onRender() {
        addModule:
        for (Module module : Sushi.getProfile().getModules().getModules(category)) {
            for (SimpleModuleComponent component : this) {
                if (component.getModule().equals(module)) continue addModule;
            }
            SimpleModuleComponent component = new SimpleModuleComponent(constants, module);
            add(component);
        }

        removeModule:
        for (SimpleModuleComponent component : this) {
            for (Module module : Sushi.getProfile().getModules().getModules(category)) {
                if (component.getModule().equals(module)) continue removeModule;
            }
            remove(component);
        }

        super.onRender();
    }

    public Category getCategory() {
        return category;
    }
}
