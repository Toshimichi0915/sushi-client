package net.toshimichi.sushi.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.toshimichi.sushi.gui.Component;

import java.awt.Color;

import static org.lwjgl.opengl.GL11.*;

public class GuiUtils {

    private static boolean locked;

    public static void lockGame() {
        if (isGameLocked()) return;
        Minecraft.getMinecraft().displayGuiScreen(new LockGuiScreen(Minecraft.getMinecraft().currentScreen));
        locked = true;
        Minecraft.getMinecraft().setIngameNotInFocus();
    }

    public static void unlockGame() {
        if (!isGameLocked()) return;
        GuiScreen screen = Minecraft.getMinecraft().currentScreen;
        if (screen instanceof LockGuiScreen)
            ((LockGuiScreen) screen).close();
        if (Minecraft.getMinecraft().currentScreen instanceof LockGuiScreen) return; // still locked!
        locked = false;
        Minecraft.getMinecraft().setIngameFocus();
    }

    public static boolean isGameLocked() {
        return locked;
    }

    public static int getWindowWidth() {
        return Minecraft.getMinecraft().displayWidth;
    }

    public static int getWindowHeight() {
        return Minecraft.getMinecraft().displayHeight;
    }

    public static int getWidth() {
        return new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth();
    }

    public static int getHeight() {
        return new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight();
    }

    public static int getScaleFactor() {
        return new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor();
    }

    public static void scissor(Component component) {
        glScissor(GuiUtils.toWindowX(component.getWindowX()) - 1, GuiUtils.getWindowHeight() - GuiUtils.toWindowY(component.getWindowY() + component.getHeight()) - 1,
                GuiUtils.toWindowX(component.getWidth()) + 1, GuiUtils.toWindowY(component.getHeight()) + 1);
    }

    public static int toWindowX(int x) {
        return (int) ((double) Minecraft.getMinecraft().displayWidth / getWidth() * x);
    }

    public static int toWindowY(int y) {
        return (int) ((double) Minecraft.getMinecraft().displayHeight / getHeight() * y);
    }

    public static int toScaledX(int x) {
        return (int) ((double) getWidth() / Minecraft.getMinecraft().displayWidth * x);
    }

    public static int toScaledY(int y) {
        return (int) (getHeight() - (double) getHeight() / Minecraft.getMinecraft().displayHeight * y);
    }

    public static void prepare2D() {
        glPushAttrib(GL_ENABLE_BIT | GL_COLOR_BUFFER_BIT);

        glEnable(GL_LINE_SMOOTH);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_CULL_FACE);
    }

    public static void release2D() {
        glPopAttrib();
    }

    public static void setColor(Color color) {
        glColor4d((double) color.getRed() / 255, (double) color.getGreen() / 255, (double) color.getBlue() / 255, (double) color.getAlpha() / 255);
    }

    public static TextPreview prepareText(String text, String font, Color color, int pts, boolean shadow) {
        if (font != null) {
            TtfTextPreview preview = TtfTextPreview.newTextPreview(text, font, color, pts, shadow);
            if (preview != null) return preview;
        }

        return new VanillaTextPreview(text, color, pts, shadow);
    }

    public static void drawLine(int x1, int y1, int x2, int y2, Color color, double width) {
        prepare2D();

        glLineWidth((float) width);
        setColor(color);
        glBegin(GL_LINES);
        glVertex2i(x1, y1);
        glVertex2i(x2, y2);
        glEnd();

        release2D();
    }

    public static void drawRect(int x, int y, int width, int height, Color color) {
        prepare2D();

        setColor(color);
        glRectd(x, y, x + width, y + height);

        release2D();
    }

    public static void drawOutline(int x, int y, int width, int height, Color color, int pts) {
        prepare2D();

        glLineWidth(pts);
        setColor(color);
        glBegin(GL_LINE_STRIP);
        glVertex2i(x, y);
        glVertex2d(x + width, y);
        glVertex2d(x + width, y + height);
        glEnd();

        release2D();
    }

}
