package net.toshimichi.sushi.gui.theme.simple;

import net.toshimichi.sushi.gui.Component;
import net.toshimichi.sushi.gui.PanelComponent;
import net.toshimichi.sushi.gui.layout.FlowDirection;
import net.toshimichi.sushi.gui.layout.FlowLayout;
import net.toshimichi.sushi.gui.theme.ThemeConstants;
import net.toshimichi.sushi.modules.Category;
import net.toshimichi.sushi.utils.GuiUtils;

public class SimpleCategoryComponent extends PanelComponent<Component> {


    private final ThemeConstants constants;
    private final Category category;

    public SimpleCategoryComponent(ThemeConstants constants, Category category) {
        this.category = category;
        this.constants = constants;
        setLayout(new FlowLayout(this, FlowDirection.DOWN));
        add(new SimpleCategoryHeaderComponent(constants, category, this));
        add(new SimpleModuleListComponent(constants, category));
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
