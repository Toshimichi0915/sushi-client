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
    private static final int MAIN_X = 50;
    private static final double SUB_X = 10;
    private static final int Y = 100;
    private final ThemeConstants constants;
    private double cursorX;
    private double cursorY;
    private float hue;
    private float saturation;
    private float brightness;
    private boolean updating;

    public SimpleColorComponent(ThemeConstants constants, Configuration<Color> configuration) {
        super(configuration);
        this.constants = constants;
        updateColor(configuration.getValue());
        configuration.addHandler(this::updateColor);
    }

    private float[] getHSB(Color color) {
        return Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
    }

    public void updateColor(Color color) {
        float[] hsb = getHSB(color);
        if (updating) return;
        cursorX = getMainX() / MAIN_X * getMainWidth() + getMainStartX() - getWindowX();
        cursorY = getMainY() / Y * getMainHeight() + getMainStartY() - getWindowY();
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
        return getWidth() - CENTER_MARGIN - RIGHT_MARGIN - SUB_X;
    }

    private double getMainHeight() {
        return getHeight() - TOP_MARGIN - BOTTOM_MARGIN;
    }

    private double getSubStartX() {
        return getWindowX() + getWidth() - LEFT_MARGIN - SUB_X;
    }

    private double getSubStartY() {
        return getWindowY() + TOP_MARGIN;
    }

    private double getSubWidth() {
        return SUB_X;
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
            cursorX = x - getWindowX();
            cursorY = y - getWindowY();
            Color color = getMainColor((x - getMainStartX()) * MAIN_X / getMainWidth(), (y - getMainStartY()) * Y / getMainHeight());
            float[] hsb = getHSB(color);
            updating = true;
            getValue().setValue(Color.getHSBColor(hue, hsb[1], hsb[2]));
            updating = false;
            saturation = hsb[1];
            brightness = hsb[2];
            return true;
        } else if (isSub(x, y)) {
            Color color = getSubColor((y - getSubStartY()) * Y / getSubHeight());
            float[] hsb = getHSB(color);
            updating = true;
            getValue().setValue(Color.getHSBColor(hsb[0], saturation, brightness));
            updating = false;
            hue = hsb[0];
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
        GuiUtils.prepare2D();
        glShadeModel(GL_SMOOTH);
        glBegin(GL_QUADS);
        GuiUtils.setColor(getMainColor(0, 0));
        glVertex2d(getMainStartX(), getMainStartY());
        GuiUtils.setColor(getMainColor(MAIN_X, 0));
        glVertex2d(getMainStartX() + getMainWidth(), getMainStartY());
        GuiUtils.setColor(getMainColor(MAIN_X, Y));
        glVertex2d(getMainStartX() + getMainWidth(), getMainStartY() + getMainHeight());
        GuiUtils.setColor(getMainColor(0, Y));
        glVertex2d(getMainStartX(), getMainStartY() + getMainHeight());
        glEnd();
        GuiUtils.release2D();

        for (int y = 0; y < Y - 1; y++) {
            double y1 = (double) y / Y * getSubHeight() + getSubStartY();
            GuiUtils.drawRect(getSubStartX(), y1, getSubWidth(), getSubHeight() / Y, getSubColor(y));
        }

        drawCircle(cursorX + getWindowX(), cursorY + getWindowY());
        drawCircle(getSubStartX() + getSubWidth() / 2, getSubY() / Y * getSubHeight() + getSubStartY());
    }

    @Override
    public void onRelocate() {
        double mainWidth = getWidth() - LEFT_MARGIN - CENTER_MARGIN - SUB_X - RIGHT_MARGIN;
        setHeight(mainWidth + TOP_MARGIN + BOTTOM_MARGIN);
    }
}
