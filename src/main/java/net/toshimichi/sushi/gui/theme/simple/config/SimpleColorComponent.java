package net.toshimichi.sushi.gui.theme.simple.config;

import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.events.input.ClickType;
import net.toshimichi.sushi.gui.MouseStatus;
import net.toshimichi.sushi.gui.base.BaseConfigComponent;
import net.toshimichi.sushi.gui.theme.ThemeConstants;
import net.toshimichi.sushi.utils.GuiUtils;

import java.awt.Color;

import static org.lwjgl.opengl.GL11.*;

public class SimpleColorComponent extends BaseConfigComponent<Color> {

    private static final int SEGMENTS = 5;
    private static final double R = 1;
    private static final double TOP_MARGIN = 0;
    private static final double LEFT_MARGIN = 0;
    private static final double BOTTOM_MARGIN = 0;
    private static final double CENTER_MARGIN = 3;
    private static final double RIGHT_MARGIN = 0;
    private static final double SUB_WIDTH = 10;
    private static final int MAIN_X = 50;
    private static final int SUB_X = 20;
    private static final int Y = 100;
    private final ThemeConstants constants;
    private float hue;
    private float saturation;
    private float brightness;

    public SimpleColorComponent(ThemeConstants constants, Configuration<Color> configuration) {
        super(configuration);
        this.constants = constants;
        updateColor(configuration.getValue());
        configuration.addHandler(this::updateColor);
    }

    public void updateColor(Color color) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        hue = hsb[0];
        saturation = hsb[1];
        brightness = hsb[2];
    }

    private Color getMainColor(double x, double y) {
        float saturation = (float) x / MAIN_X;
        float brightness = 1 - (float) y / Y;
        return Color.getHSBColor(hue, saturation, brightness);
    }

    private Color getSubColor(double y) {
        float hue = (float) y / Y;
        return Color.getHSBColor(hue, 1, 1);
    }

    private double getMainX() {
        return saturation * MAIN_X;
    }

    private double getMainY() {
        return (1 - brightness) * Y;
    }

    private double getSubY() {
        return hue * Y;
    }

    private double getMainStartX() {
        return getWindowX() + LEFT_MARGIN;
    }

    private double getMainStartY() {
        return getWindowY() + TOP_MARGIN;
    }

    private double getMainWidth() {
        return getWidth() - CENTER_MARGIN - RIGHT_MARGIN - SUB_WIDTH;
    }

    private double getMainHeight() {
        return getHeight() - TOP_MARGIN - BOTTOM_MARGIN;
    }

    private double getSubStartX() {
        return getWindowX() + getWidth() - LEFT_MARGIN - SUB_WIDTH;
    }

    private double getSubStartY() {
        return getWindowY() + TOP_MARGIN;
    }

    private double getSubWidth() {
        return SUB_WIDTH;
    }

    private double getSubHeight() {
        return getHeight() - TOP_MARGIN - BOTTOM_MARGIN;
    }

    private boolean isMain(double x, double y) {
        return getMainStartX() <= x &&
                getMainStartY() <= y &&
                x <= getMainStartX() + getMainWidth() &&
                y <= getMainStartY() + getMainHeight();
    }

    private boolean isSub(int x, int y) {
        return getSubStartX() <= x &&
                getSubStartY() <= y &&
                x <= getSubStartX() + getSubWidth() &&
                y <= getSubStartY() + getSubHeight();
    }

    private void drawCircle(double oX, double oY) {
        GuiUtils.prepare2D();
        GuiUtils.setColor(new Color(255, 255, 255));
        glBegin(GL_LINE_LOOP);
        for (int i = 0; i < SEGMENTS; i++) {
            double theta = 2 * Math.PI * i / SEGMENTS;
            double x = R * Math.cos(theta);
            double y = R * Math.sin(theta);
            glVertex2d(x + oX, y + oY);
        }
        glEnd();
        GuiUtils.release2D();
    }

    private boolean updateColor(int x, int y) {
        if (isMain(x, y)) {
            Color color = getMainColor((x - getMainStartX()) * MAIN_X / getMainWidth(), (y - getMainStartY()) * Y / getMainHeight());
            getValue().setValue(color);
            return true;
        } else if (isSub(x, y)) {
            Color color = getSubColor((y - getSubStartY()) * Y / getSubHeight());
            getValue().setValue(color);
            return true;
        }
        return false;
    }

    @Override
    public void onClick(int x, int y, ClickType type) {
        if (!updateColor(x, y)) super.onClick(x, y, type);
    }

    @Override
    public void onHold(int fromX, int fromY, int toX, int toY, ClickType type, MouseStatus status) {
        if (!updateColor(fromX, fromY)) super.onHold(fromX, fromY, toX, toY, type, status);
    }

    @Override
    public void onRender() {
        GuiUtils.drawRect(getWindowX(), getWindowY(), getWidth(), getHeight(), constants.backgroundColor.getValue());
        for (int x = 0; x < MAIN_X - 1; x++) {
            for (int y = 0; y < Y - 1; y++) {
                double x1 = (double) x / MAIN_X * getMainWidth() + getMainStartX();
                double y1 = (double) y / Y * getMainHeight() + getMainStartY();
                GuiUtils.drawRect(x1, y1, getMainWidth() / MAIN_X, getMainHeight() / Y, getMainColor(x, y));
            }
        }

        for (int y = 0; y < Y - 1; y++) {
            double y1 = (double) y / Y * getSubHeight() + getSubStartY();
            GuiUtils.drawRect(getSubStartX(), y1, getSubWidth(), getSubHeight() / Y, getSubColor(y));
        }

        drawCircle(getMainX() / MAIN_X * getMainWidth() + getMainStartX(), getMainY() / Y * getMainHeight() + getMainStartY());
        drawCircle(getSubStartX() + getSubWidth() / 2, getSubY() / Y * getSubHeight() + getSubStartY());
    }

    @Override
    public void onRelocate() {
        double mainWidth = getWidth() - LEFT_MARGIN - CENTER_MARGIN - SUB_WIDTH - RIGHT_MARGIN;
        setHeight(mainWidth + TOP_MARGIN + BOTTOM_MARGIN);
    }
}
