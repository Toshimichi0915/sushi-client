package net.toshimichi.sushi.gui.theme.simple;

import net.toshimichi.sushi.gui.Component;
import net.toshimichi.sushi.gui.PanelComponent;
import net.toshimichi.sushi.gui.layout.FlowDirection;
import net.toshimichi.sushi.gui.layout.FlowLayout;
import net.toshimichi.sushi.gui.theme.Theme;
import net.toshimichi.sushi.gui.theme.ThemeConstants;
import net.toshimichi.sushi.modules.Category;

public class SimpleCategoryComponent extends PanelComponent<Component> {


    private final Category category;
    private final ThemeConstants constants;
    private final Theme theme;

    public SimpleCategoryComponent(ThemeConstants constants, Theme theme, Category category) {
        this.category = category;
        this.constants = constants;
        this.theme = theme;
        setLayout(new FlowLayout(this, FlowDirection.DOWN));
        add(new SimpleModuleHeaderComponent(constants, category, this));
        add(new SimpleModuleListComponent(constants, category, theme));
    }

    public Category getCategory() {
        return category;
    }
}
