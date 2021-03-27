package net.toshimichi.sushi.gui.theme.simple;

import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.Configurations;
import net.toshimichi.sushi.gui.Component;
import net.toshimichi.sushi.gui.PanelComponent;
import net.toshimichi.sushi.gui.layout.FlowDirection;
import net.toshimichi.sushi.gui.layout.FlowLayout;
import net.toshimichi.sushi.gui.theme.Theme;
import net.toshimichi.sushi.gui.theme.ThemeConstants;
import net.toshimichi.sushi.modules.Category;
import net.toshimichi.sushi.utils.GuiUtils;

public class SimpleCategoryComponent extends PanelComponent<Component> {

    private final ThemeConstants constants;
    private final Category category;

    private final Configuration<Integer> x;
    private final Configuration<Integer> y;

    public SimpleCategoryComponent(ThemeConstants constants, Theme theme, Configurations configurations, Category category, int defaultX, int defaultY) {
        this.constants = constants;
        this.category = category;
        setLayout(new FlowLayout(this, FlowDirection.DOWN));
        add(new SimpleCategoryHeaderComponent(constants, category, this));
        add(new SimpleModuleListComponent(constants, theme, category));
        x = configurations.get("gui.category." + category.getName().toLowerCase() + ".x",
                category.getName() + " X", "x coordinates of " + category.getName(), Integer.class, defaultX);
        y = configurations.get("gui.category." + category.getName().toLowerCase() + ".y",
                category.getName() + " Y", "y coordinates of " + category.getName(), Integer.class, defaultY);
    }

    @Override
    public int getX() {
        return x.getValue();
    }

    @Override
    public int getY() {
        return y.getValue();
    }

    @Override
    public void setX(int x) {
        this.x.setValue(x);
    }

    @Override
    public void setY(int y) {
        this.y.setValue(y);
    }

    @Override
    public void onRender() {
        GuiUtils.drawRect(getWindowX(), getWindowY(), getWidth(), getHeight(), constants.backgroundColor.getValue());
        super.onRender();
    }

    public Category getCategory() {
        return category;
    }
}
