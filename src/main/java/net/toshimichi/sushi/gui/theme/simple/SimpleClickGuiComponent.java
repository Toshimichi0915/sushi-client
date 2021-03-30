package net.toshimichi.sushi.gui.theme.simple;

import net.toshimichi.sushi.Sushi;
import net.toshimichi.sushi.config.Configurations;
import net.toshimichi.sushi.gui.base.BasePanelComponent;
import net.toshimichi.sushi.gui.theme.Theme;
import net.toshimichi.sushi.gui.theme.ThemeConstants;
import net.toshimichi.sushi.modules.Category;
import net.toshimichi.sushi.modules.Module;
import net.toshimichi.sushi.utils.GuiUtils;


public class SimpleClickGuiComponent extends BasePanelComponent<SimpleCategoryComponent> {

    private final ThemeConstants constants;
    private final Theme theme;
    private final Configurations configurations;
    private final Module module;

    public SimpleClickGuiComponent(ThemeConstants constants, Theme theme, Configurations configurations, Module module) {
        this.constants = constants;
        this.theme = theme;
        this.configurations = configurations;
        this.module = module;
        find();
    }

    @Override
    public void setFocusedComponent(SimpleCategoryComponent component) {
        super.setFocusedComponent(component);
        remove(component);
        add(0, component);
    }

    @Override
    public void onRender() {
        setWidth(GuiUtils.getWidth());
        setHeight(GuiUtils.getHeight());
        find();

        super.onRender();
    }

    private void find() {
        addCategory:
        for (Category category : Sushi.getProfile().getCategories().getAll()) {
            for (SimpleCategoryComponent component : this) {
                if (component.getCategory().equals(category)) continue addCategory;
            }
            SimpleCategoryComponent newComponent = new SimpleCategoryComponent(constants, theme, configurations, category, size() * 102 + 50, 20);
            add(newComponent, true);
            newComponent.setWidth(100);
        }
    }

    @Override
    public void onClose() {
        module.setEnabled(false);
    }
}
