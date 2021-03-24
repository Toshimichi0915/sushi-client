package net.toshimichi.sushi.gui.theme.simple;

import net.toshimichi.sushi.gui.Component;
import net.toshimichi.sushi.gui.PanelComponent;
import net.toshimichi.sushi.gui.layout.FlowDirection;
import net.toshimichi.sushi.gui.layout.FlowLayout;
import net.toshimichi.sushi.gui.theme.ThemeConstants;
import net.toshimichi.sushi.modules.Category;

public class SimpleCategoryComponent extends PanelComponent<Component> {


    private final Category category;
    private final ThemeConstants constants;

    public SimpleCategoryComponent(ThemeConstants constants, Category category) {
        this.category = category;
        this.constants = constants;
        setLayout(new FlowLayout(this, FlowDirection.DOWN));
        add(new SimpleCategoryHeaderComponent(constants, category, this));
        add(new SimpleModuleListComponent(constants, category));
    }

    public Category getCategory() {
        return category;
    }
}
