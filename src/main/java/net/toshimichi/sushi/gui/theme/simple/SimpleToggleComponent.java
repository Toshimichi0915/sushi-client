package net.toshimichi.sushi.gui.theme.simple;

import net.toshimichi.sushi.events.input.ClickType;
import net.toshimichi.sushi.gui.MouseStatus;
import net.toshimichi.sushi.gui.base.BaseSettingComponent;
import net.toshimichi.sushi.gui.theme.ThemeConstants;
import net.toshimichi.sushi.utils.render.GuiUtils;

import java.awt.Color;

abstract public class SimpleToggleComponent<T> extends BaseSettingComponent<T> {

    private final ThemeConstants constants;
    private boolean current;
    private boolean hover;

    public SimpleToggleComponent(ThemeConstants constants, boolean current) {
        this.constants = constants;
        this.current = current;
    }

    public boolean isToggled() {
        return current;
    }

    public void setToggled(boolean current) {
        if (this.current == current) return;
        onChange(current);
        this.current = current;
    }

    @Override
    public void onRender() {
        Color color;
        if (hover) {
            if (current) color = constants.selectedHoverColor.getValue();
            else color = constants.unselectedHoverColor.getValue();
        } else {
            if (current) color = constants.enabledColor.getValue();
            else color = constants.disabledColor.getValue();
        }
        hover = false;
        GuiUtils.drawRect(getWindowX(), getWindowY(), getWidth(), getHeight(), color);
    }

    @Override
    public void onClick(int x, int y, ClickType type) {
        if (type == ClickType.RIGHT) return;
        setToggled(!current);
    }

    @Override
    public void onHold(int fromX, int fromY, int toX, int toY, ClickType type, MouseStatus status) {
        if (status != MouseStatus.END || type == ClickType.RIGHT) return;
        setToggled(!current);
    }

    @Override
    public void onHover(int x, int y) {
        hover = true;
    }

    protected void onChange(boolean newValue) {
    }
}
