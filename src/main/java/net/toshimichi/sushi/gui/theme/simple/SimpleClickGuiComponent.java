package net.toshimichi.sushi.gui.theme.simple;

import net.toshimichi.sushi.Sushi;
import net.toshimichi.sushi.gui.PanelComponent;
import net.toshimichi.sushi.gui.theme.ThemeConstants;
import net.toshimichi.sushi.modules.Category;
import net.toshimichi.sushi.modules.Module;
import net.toshimichi.sushi.utils.GuiUtils;


public class SimpleClickGuiComponent extends PanelComponent<SimpleCategoryComponent> {

    private final Module module;
    private final ThemeConstants constants;

    public SimpleClickGuiComponent(ThemeConstants constants, Module module) {
        this.constants = constants;
        this.module = module;
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
        addCategory:
        for (Category category : Sushi.getProfile().getCategories().getAll()) {
            for (SimpleCategoryComponent component : this) {
                if (component.getCategory().equals(category)) continue addCategory;
            }
            SimpleCategoryComponent newComponent = new SimpleCategoryComponent(constants, category);
            add(newComponent);
            newComponent.setWidth(100);
            newComponent.setX(size() * 102 - 50);
            newComponent.setY(20);
        }

        super.onRender();
    }

    @Override
    public void onClose() {
        super.onClose();
        module.setEnabled(false);
    }
}
