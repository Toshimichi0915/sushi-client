package net.sushiclient.client.gui.theme.simple;

import net.minecraft.util.math.MathHelper;
import net.sushiclient.client.events.input.ClickType;
import net.sushiclient.client.gui.MouseStatus;
import net.sushiclient.client.gui.base.BaseComponent;
import net.sushiclient.client.gui.theme.ThemeConstants;
import net.sushiclient.client.utils.render.GuiUtils;

import java.awt.Color;

abstract public class SimpleBarComponent extends BaseComponent {

    private final ThemeConstants constants;
    private double progress;
    private boolean hover;

    public SimpleBarComponent(ThemeConstants constants, double progress) {
        this.constants = constants;
        this.progress = progress;
        setHeight(12);
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
        Color color1;
        Color color2;
        if (hover) {
            color1 = constants.unselectedHoverColor.getValue();
            color2 = constants.selectedHoverColor.getValue();
        } else {
            color1 = constants.disabledColor.getValue();
            color2 = constants.barColor.getValue();
        }
        GuiUtils.drawRect(getWindowX(), getWindowY(), getWidth(), getHeight(), color1);
        GuiUtils.drawRect(getWindowX(), getWindowY(), (int) (getWidth() * progress), getHeight(), color2);
        hover = false;
    }

    @Override
    public void onClick(int x, int y, ClickType type) {
        double progress = (x - getWindowX()) / getWidth();
        progress = MathHelper.clamp(progress, 0, 1);
        setProgress(progress);
    }

    @Override
    public void onHold(int fromX, int fromY, int toX, int toY, ClickType type, MouseStatus status) {
        onClick(toX, toY, type);
    }

    @Override
    public void onHover(int x, int y) {
        hover = true;
    }

    protected void onChange(double newProgress) {
    }
}
