package net.toshimichi.sushi.gui.theme.simple;

import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.ConfigurationCategory;
import net.toshimichi.sushi.events.input.ClickType;
import net.toshimichi.sushi.gui.Insets;
import net.toshimichi.sushi.gui.MouseStatus;
import net.toshimichi.sushi.gui.SmoothCollapseComponent;
import net.toshimichi.sushi.gui.base.BaseSettingComponent;
import net.toshimichi.sushi.gui.theme.ThemeConstants;
import net.toshimichi.sushi.utils.render.GuiUtils;
import net.toshimichi.sushi.utils.render.TextPreview;

import java.awt.Color;

public class SimpleConfigCategoryHeaderComponent extends BaseSettingComponent<ConfigurationCategory> {

    private final ThemeConstants constants;
    private final ConfigurationCategory configCategory;
    private final SmoothCollapseComponent<?> component;
    private boolean hover;

    public SimpleConfigCategoryHeaderComponent(ThemeConstants constants, ConfigurationCategory configCategory, SmoothCollapseComponent<?> component) {
        this.constants = constants;
        this.configCategory = configCategory;
        this.component = component;
        setHeight(14);
        setMargin(new Insets(2, 2, 2, 2));
    }

    @Override
    public void onRender() {
        Configuration<Color> color;
        if (!component.isCollapsed()) {
            if (hover) color = constants.selectedHoverColor;
            else color = constants.enabledColor;
        } else {
            if (hover) color = constants.unselectedHoverColor;
            else color = constants.disabledColor;
        }
        GuiUtils.drawRect(getWindowX(), getWindowY(), getWidth(), getHeight(), color.getValue());
        TextPreview preview = GuiUtils.prepareText(configCategory.getName(), constants.font.getValue(), constants.textColor.getValue(), 10, true);
        preview.draw(getWindowX() + (getWidth() - preview.getWidth()) / 2 - 1, getWindowY() + 1);
        hover = false;
    }

    @Override
    public void onClick(int x, int y, ClickType type) {
        component.setCollapsed(!component.isCollapsed());
    }

    @Override
    public void onHold(int fromX, int fromY, int toX, int toY, ClickType type, MouseStatus status) {
        if (status != MouseStatus.END) return;
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
