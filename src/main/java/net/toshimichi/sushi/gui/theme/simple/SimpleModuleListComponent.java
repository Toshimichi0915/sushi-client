package net.toshimichi.sushi.gui.theme.simple;

import net.toshimichi.sushi.Sushi;
import net.toshimichi.sushi.gui.Component;
import net.toshimichi.sushi.gui.PanelComponent;
import net.toshimichi.sushi.gui.theme.ThemeConstants;
import net.toshimichi.sushi.modules.Category;
import net.toshimichi.sushi.modules.Module;
import net.toshimichi.sushi.utils.GuiUtils;

public class SimpleModuleListComponent extends PanelComponent {

    private static final int MARGIN_1 = 10;
    private static final int MARGIN_2 = 5;

    private final Category category;
    private final ThemeConstants constants;

    public SimpleModuleListComponent(Category category, ThemeConstants constants) {
        this.category = category;
        this.constants = constants;
    }

    @Override
    public void onRender() {
        addModule:
        for (Module module : Sushi.getProfile().getModules().getModules(category)) {
            for (Component component : this) {
                if (!(component instanceof SimpleModuleComponent)) continue;
                if (((SimpleModuleComponent) component).getModule().equals(module)) continue addModule;
            }
            SimpleModuleComponent component = new SimpleModuleComponent(module, constants);
            component.setOrigin(this);
            component.setWidth(getWidth());
            component.setVisible(true);
            add(component);
        }

        int currentY = 0;
        int height = 12;
        GuiUtils.drawRect(getWindowX(), getWindowY(), getWidth(), height, constants.menuBarColor.getValue());
        GuiUtils.drawText(getWindowX() + 12, getWindowY() + 2, category.getName(), null, constants.textColor.getValue(), 9, false);
        currentY += height;

        for (Component c : this) {
            if (!(c instanceof SimpleModuleComponent)) continue;
            SimpleModuleComponent component = (SimpleModuleComponent) c;
            component.setY(currentY);
            component.onRender();
            currentY += component.getHeight();
        }
        setHeight(currentY);
    }

    public Category getCategory() {
        return category;
    }
}
