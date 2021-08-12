package net.sushiclient.client.gui.theme.simple;

import net.minecraft.client.renderer.GlStateManager;
import net.sushiclient.client.events.input.ClickType;
import net.sushiclient.client.gui.MouseStatus;
import net.sushiclient.client.gui.base.BaseComponent;
import net.sushiclient.client.gui.theme.ThemeConstants;
import net.sushiclient.client.utils.render.GuiUtils;
import net.sushiclient.client.utils.render.TextPreview;
import org.lwjgl.opengl.GL11;

import java.awt.Color;

public class SimpleColorPickerComponent extends BaseComponent {

    private static final int SEGMENTS = 5;
    private static final int SEGMENTS_X = 10;
    private static final int SEGMENTS_Y = 10;
    private static final double R = 1;
    private static final double MARGIN_TOP = 10;
    private static final double MARGIN_LEFT = 2;
    private static final double MARGIN_BOTTOM = 2;
    private static final double MARGIN_CENTER = 2;
    private static final double MARGIN_RIGHT = 2;
    private static final int MAIN_X = 50;
    private static final double SUB_X = 10;
    private static final int Y = 100;
    private final ThemeConstants constants;
    private final String name;
    private Color color;
    private double cursorX;
    private double cursorY;
    private float hue;
    private float saturation;
    private float brightness;
    private double oldX;
    private double oldY;
    private double oldWidth;
    private double oldHeight;

    public SimpleColorPickerComponent(ThemeConstants constants, String name, Color color) {
        this.constants = constants;
        this.name = name;
        this.color = color;
    }

    public double getMarginTop() {
        return MARGIN_TOP;
    }

    public double getMarginLeft() {
        return MARGIN_LEFT;
    }

    public double getMarginBottom() {
        return MARGIN_BOTTOM;
    }

    public double getMarginCenter() {
        return MARGIN_CENTER;
    }

    public double getMarginRight() {
        return MARGIN_RIGHT;
    }

    public int getMainX() {
        return MAIN_X;
    }

    public double getSubX() {
        return SUB_X;
    }

    public int getMainY() {
        return Y;
    }

    private float[] getHSB(Color color) {
        return Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
    }

    private Color getMainColor(double x, double y) {
        float saturation = (float) x / getMainX();
        float brightness = 1 - (float) y / getMainY();
        return Color.getHSBColor(hue, saturation, brightness);
    }

    private Color getSubColor(double y) {
        float hue = (float) y / getMainY();
        return Color.getHSBColor(hue, 1, 1);
    }

    private double getCursorMainX() {
        return saturation * getMainX();
    }

    private double getCursorMainY() {
        return (1 - brightness) * getMainY();
    }

    private double getCursorSubY() {
        return hue * getMainY();
    }

    private double getMainStartX() {
        return getWindowX() + getMarginLeft();
    }

    private double getMainStartY() {
        return getWindowY() + getMarginTop();
    }

    private double getMainWidth() {
        return getWidth() - getMarginLeft() - getMarginCenter() - getMarginRight() - getSubX();
    }

    private double getMainHeight() {
        return getHeight() - getMarginTop() - getMarginBottom();
    }

    private double getSubStartX() {
        return getWindowX() + getWidth() - getMarginLeft() - getSubX();
    }

    private double getSubStartY() {
        return getWindowY() + getMarginTop();
    }

    private double getSubWidth() {
        return getSubX();
    }

    private double getSubHeight() {
        return getHeight() - getMarginTop() - getMarginBottom();
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
        GlStateManager.glBegin(GL11.GL_LINE_LOOP);
        for (int i = 0; i < SEGMENTS; i++) {
            double theta = 2 * Math.PI * i / SEGMENTS;
            double x = R * Math.cos(theta);
            double y = R * Math.sin(theta);
            GlStateManager.glVertex3f((float) (x + oX), (float) (y + oY), 0);
        }
        GlStateManager.glEnd();
        GuiUtils.release2D();
    }

    private boolean updateColor(int x, int y) {
        if (isMain(x, y)) {
            cursorX = x - getMainStartX();
            cursorY = y - getMainStartY();
            Color color = getMainColor((x - getMainStartX()) * getMainX() / getMainWidth(), (y - getMainStartY()) * getMainY() / getMainHeight());
            float[] hsb = getHSB(color);
            color = Color.getHSBColor(hue, hsb[1], hsb[2]);
            onChange(color);
            saturation = hsb[1];
            brightness = hsb[2];
            return true;
        } else if (isSub(x, y)) {
            Color color = getSubColor((y - getSubStartY()) * getMainY() / getSubHeight());
            float[] hsb = getHSB(color);
            color = Color.getHSBColor(hsb[0], saturation, brightness);
            onChange(color);
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

    private void vertex(double x, double y) {
        GlStateManager.glVertex3f((float) x, (float) y, 0);
    }

    @Override
    public void onRender() {
        GuiUtils.drawRect(getWindowX(), getWindowY(), getWidth(), getHeight(), constants.outlineColor.getValue());
        TextPreview preview = GuiUtils.prepareText(name, constants.font.getValue(), constants.textColor.getValue(), 9, true);
        preview.draw(getWindowX() + (getWidth() - preview.getWidth()) / 2 - 1, getWindowY() + (getMarginTop() - preview.getHeight()) / 2 - 1);

        // main
        GuiUtils.prepare2D();
        for (int x = 0; x < SEGMENTS_X; x++) {
            for (int y = 0; y < SEGMENTS_Y; y++) {
                GlStateManager.shadeModel(GL11.GL_SMOOTH);
                GlStateManager.glBegin(GL11.GL_QUADS);
                GuiUtils.setColor(getMainColor((double) x / SEGMENTS_X * getMainX(), (double) y / SEGMENTS_Y * getMainY()));
                vertex((double) x / SEGMENTS_X * getMainWidth() + getMainStartX(), (double) y / SEGMENTS_Y * getMainHeight() + getMainStartY());
                GuiUtils.setColor(getMainColor((double) (x + 1) / SEGMENTS_X * getMainX(), (double) y / SEGMENTS_Y * getMainY()));
                vertex((double) (x + 1) / SEGMENTS_X * getMainWidth() + getMainStartX(), (double) y / SEGMENTS_Y * getMainHeight() + getMainStartY());
                GuiUtils.setColor(getMainColor((double) (x + 1) / SEGMENTS_X * getMainX(), (double) (y + 1) / SEGMENTS_Y * getMainY()));
                vertex((double) (x + 1) / SEGMENTS_X * getMainWidth() + getMainStartX(), (double) (y + 1) / SEGMENTS_Y * getMainHeight() + getMainStartY());
                GuiUtils.setColor(getMainColor((double) x / SEGMENTS_X * getMainX(), (double) (y + 1) / SEGMENTS_Y * getMainY()));
                vertex((double) x / SEGMENTS_X * getMainWidth() + getMainStartX(), (double) (y + 1) / SEGMENTS_Y * getMainHeight() + getMainStartY());
                GlStateManager.glEnd();
            }
        }
        GuiUtils.release2D();

        // sub
        for (int y = 0; y < getMainY() - 1; y++) {
            double y1 = (double) y / getMainY() * getSubHeight() + getSubStartY();
            GuiUtils.drawRect(getSubStartX(), y1, getSubWidth(), getSubHeight() / getMainY(), getSubColor(y));
        }

        // circle
        drawCircle(cursorX + getMainStartX(), cursorY + getMainStartY());
        drawCircle(getSubStartX() + getSubWidth() / 2, getCursorSubY() / getMainY() * getSubHeight() + getSubStartY());
    }

    protected void resize() {
        setHeight(getMainWidth() - 10 + getMarginTop() + getMarginBottom());
    }

    @Override
    public void onRelocate() {
        resize();
        if (oldX != getX() || oldY != getY() || oldWidth != getWidth() || oldHeight != getHeight()) {
            oldX = getX();
            oldY = getY();
            oldWidth = getWidth();
            oldHeight = getHeight();
            setColor(color);
        }
    }

    protected void onChange(Color color) {}


    public void setColor(Color color) {
        this.color = color;
        float[] hsb = getHSB(color);
        hue = hsb[0];
        saturation = hsb[1];
        brightness = hsb[2];
        cursorX = getCursorMainX() / getMainX() * getMainWidth();
        cursorY = getCursorMainY() / getMainY() * getMainHeight();
    }
}
