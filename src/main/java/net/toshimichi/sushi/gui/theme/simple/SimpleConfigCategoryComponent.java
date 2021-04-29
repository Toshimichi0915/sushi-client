package net.toshimichi.sushi.gui.theme.simple;

import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.ConfigurationCategory;
import net.toshimichi.sushi.events.input.ClickType;
import net.toshimichi.sushi.gui.Insets;
import net.toshimichi.sushi.gui.MouseStatus;
import net.toshimichi.sushi.gui.SmoothCollapseComponent;
import net.toshimichi.sushi.gui.base.BaseSettingComponent;
import net.toshimichi.sushi.gui.theme.ThemeConstants;
import net.toshimichi.sushi.utils.GuiUtils;

import java.awt.Color;

public class SimpleConfigCategoryComponent extends BaseSettingComponent<ConfigurationCategory> {

    private final ThemeConstants constants;
    private final ConfigurationCategory configCategory;
    private final SmoothCollapseComponent<?> component;
    private boolean hover;

    public SimpleConfigCategoryComponent(ThemeConstants constants, ConfigurationCategory configCategory, SmoothCollapseComponent<?> component) {
        this.constants = constants;
        this.configCategory = configCategory;
        this.component = component;
        setHeight(16);
        setMargin(new Insets(0, 2, 0, 2));
    }

    @Override
    public void onRender() {
        GuiUtils.prepareArea(this);
        Configuration<Color> color;
        if (component.isCollapsed()) {
            if (hover) color = constants.selectedHoverColor;
            else color = constants.enabledColor;
        } else {
            if (hover) color = constants.unselectedHoverColor;
            else color = constants.disabledColor;
        }
        GuiUtils.drawRect(getWindowX(), getWindowY(), getWidth(), getHeight(), color.getValue());
        GuiUtils.prepareText(configCategory.getName(), constants.font.getValue(), constants.textColor.getValue(), 10, true)
                .draw(getWindowX() + 5, getWindowY() + 3);
        GuiUtils.releaseArea();

        hover = false;
    }

    @Override
    public void onClick(int x, int y, ClickType type) {
        if (type != ClickType.RIGHT) return;
        component.setCollapsed(!component.isCollapsed());
    }

    @Override
    public void onHold(int fromX, int fromY, int toX, int toY, ClickType type, MouseStatus status) {
        if (status != MouseStatus.END || type != ClickType.RIGHT) return;
        component.setCollapsed(!component.isCollapsed());
    }

    @Override
    public void onHover(int x, int y) {
        hover = true;
    }

    @Override
    public ConfigurationCategory getValue() {
        return configCategory;
    }
}
