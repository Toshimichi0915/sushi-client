package net.toshimichi.sushi.gui.clickgui;

import net.toshimichi.sushi.Sushi;
import net.toshimichi.sushi.gui.Anchor;
import net.toshimichi.sushi.gui.BaseComponent;
import net.toshimichi.sushi.modules.Category;
import net.toshimichi.sushi.modules.Module;
import net.toshimichi.sushi.modules.config.Configuration;
import net.toshimichi.sushi.modules.config.Configurations;
import net.toshimichi.sushi.utils.RenderUtils;

import java.awt.*;
import java.util.ArrayList;

public class ModuleListComponent extends BaseComponent {

    private static final int MARGIN_1 = 10;
    private static final int MARGIN_2 = 5;

    private final Configurations configurations;
    private final RenderUtils utils;
    private final Category category;
    private final ArrayList<ModuleComponent> components = new ArrayList<>();

    public ModuleListComponent(Category category, Configurations configurations, RenderUtils utils) {
        this.category = category;
        this.configurations = configurations;
        this.utils = utils;
    }

    @Override
    public void onRender() {
        addModule: for(Module module : Sushi.getProfile().getModules().getModules(category)) {
            for(ModuleComponent component : components) {
                if(component.getModule().equals(module)) continue addModule;
            }
            ModuleComponent component = new ModuleComponent(module, configurations, utils);
            component.setOrigin(this);
            component.setAnchor(Anchor.TOP_LEFT);
            component.setX(0);
            component.setWidth(getWidth());
            components.add(component);
        }

        int currentY = 0;
        int height = 20;
        utils.drawRect(getWindowX(), getWindowY(), getWidth(), height, new Color(255, 0, 0));
        utils.drawText(getWindowX() + 10, getWindowY() + 10, category.getName(), null, new Color(0, 0, 0), 10, false);
        currentY += height;

        for(ModuleComponent component : components) {
            component.setY(currentY);
            component.onRender();
            currentY += component.getHeight();
        }
    }

    public Category getCategory() {
        return category;
    }
}
