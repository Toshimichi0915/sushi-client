package net.toshimichi.sushi.gui.theme.simple;

import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.Configurations;
import net.toshimichi.sushi.gui.AnyPanelComponent;
import net.toshimichi.sushi.gui.layout.FlowDirection;
import net.toshimichi.sushi.gui.layout.FlowLayout;
import net.toshimichi.sushi.gui.theme.Theme;
import net.toshimichi.sushi.gui.theme.ThemeConstants;
import net.toshimichi.sushi.modules.Category;
import net.toshimichi.sushi.utils.render.GuiUtils;

public class SimpleCategoryComponent extends AnyPanelComponent {

    private final ThemeConstants constants;
    private final Category category;

    private final Configuration<Double> x;
    private final Configuration<Double> y;

    public SimpleCategoryComponent(ThemeConstants constants, Theme theme, Configurations configurations, Category category, double defaultX, double defaultY) {
        this.constants = constants;
        this.category = category;
        setLayout(new FlowLayout(this, FlowDirection.DOWN));
        add(new SimpleCategoryHeaderComponent(constants, category, this));
        add(new SimpleModuleListComponent(constants, theme, category));
        x = configurations.get("gui.category." + category.getName().toLowerCase() + ".x",
                category.getName() + " X", "X coordinate of " + category.getName(), Double.class, defaultX);
        y = configurations.get("gui.category." + category.getName().toLowerCase() + ".y",
                category.getName() + " Y", "Y coordinate of " + category.getName(), Double.class, defaultY);
    }

    @Override
    public double getX() {
        return x.getValue();
    }

    @Override
    public double getY() {
        return y.getValue();
    }

    @Override
    public void setX(double x) {
        this.x.setValue(x);
    }

    @Override
    public void setY(double y) {
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
