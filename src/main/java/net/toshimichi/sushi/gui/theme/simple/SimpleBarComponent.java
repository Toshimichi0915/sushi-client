package net.toshimichi.sushi.gui.theme.simple;

import net.minecraft.util.math.MathHelper;
import net.toshimichi.sushi.events.input.ClickType;
import net.toshimichi.sushi.gui.MouseStatus;
import net.toshimichi.sushi.gui.base.BaseSettingComponent;
import net.toshimichi.sushi.gui.theme.ThemeConstants;
import net.toshimichi.sushi.utils.GuiUtils;

abstract public class SimpleBarComponent<T> extends BaseSettingComponent<T> {

    private final ThemeConstants constants;
    private double progress;

    public SimpleBarComponent(ThemeConstants constants, double progress) {
        this.constants = constants;
        this.progress = progress;
        setHeight(14);
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        onChange(progress);
        this.progress = progress;
    }

    @Override
    public void onRender() {
        GuiUtils.drawRect(getWindowX(), getWindowY(), getWidth(), getHeight(), constants.barBackgroundColor.getValue());
        GuiUtils.drawRect(getWindowX(), getWindowY(), (int) (getWidth() * progress), getHeight(), constants.barColor.getValue());
    }

    @Override
    public void onClick(int x, int y, ClickType type) {
        double progress = (double) (x - getWindowX()) / getWidth();
        progress = MathHelper.clamp(progress, 0, 1);
        setProgress(progress);
    }

    @Override
    public void onHold(int fromX, int fromY, int toX, int toY, ClickType type, MouseStatus status) {
        onClick(toX, toY, type);
    }

    protected void onChange(double newProgress) {
    }
}
