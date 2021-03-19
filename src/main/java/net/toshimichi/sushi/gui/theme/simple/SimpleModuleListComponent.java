package net.toshimichi.sushi.gui.theme.simple;

import net.toshimichi.sushi.Sushi;
import net.toshimichi.sushi.gui.base.BaseComponent;
import net.toshimichi.sushi.modules.Category;
import net.toshimichi.sushi.modules.Module;
import net.toshimichi.sushi.modules.config.Configurations;
import net.toshimichi.sushi.utils.GuiUtils;

import java.awt.Color;
import java.util.ArrayList;

public class SimpleModuleListComponent extends BaseComponent {

    private static final int MARGIN_1 = 10;
    private static final int MARGIN_2 = 5;

    private final Configurations configurations;
    private final Category category;
    private final ArrayList<SimpleModuleComponent> components = new ArrayList<>();

    public SimpleModuleListComponent(Category category, Configurations configurations) {
        this.category = category;
        this.configurations = configurations;
    }

    @Override
    public void onRender() {
        addModule:
        for (Module module : Sushi.getProfile().getModules().getModules(category)) {
            for (SimpleModuleComponent component : components) {
                if (component.getModule().equals(module)) continue addModule;
            }
            SimpleModuleComponent component = new SimpleModuleComponent(module, configurations);
            component.setOrigin(this);
            component.setWidth(getWidth());
            component.setVisible(true);
            components.add(component);
        }

        int currentY = 0;
        int height = 20;
        GuiUtils.drawRect(getWindowX(), getWindowY(), getWidth(), height, new Color(255, 0, 0));
        GuiUtils.drawText(getWindowX() + 10, getWindowY() + 10, category.getName(), null, new Color(0, 0, 0), 10, false);
        currentY += height;

        for (SimpleModuleComponent component : components) {
            component.setY(currentY);
            component.onRender();
            currentY += component.getHeight();
        }
    }

    public Category getCategory() {
        return category;
    }
}
