package net.toshimichi.sushi.utils.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.toshimichi.sushi.config.data.EspColor;
import net.toshimichi.sushi.gui.Component;
import net.toshimichi.sushi.utils.VanillaTextPreview;

import java.awt.Color;
import java.util.Stack;

import static org.lwjgl.opengl.GL11.*;

public class GuiUtils {

    private static boolean locked;
    private static final Stack<Scissor> scissorStack = new Stack<>();

    public static void lockGame(Runnable onClose) {
        if (isGameLocked()) return;
        Minecraft.getMinecraft().displayGuiScreen(new LockGuiScreen(Minecraft.getMinecraft().currentScreen, onClose));
        locked = true;
        Minecraft.getMinecraft().setIngameNotInFocus();
    }

    public static void lockGame() {
        lockGame(() -> {
        });
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

    public static void prepareArea(Component component) {
        glPushAttrib(GL_SCISSOR_BIT);
        glEnable(GL_SCISSOR_TEST);
        Scissor scissor = new Scissor(component);
        if (!scissorStack.isEmpty())
            scissor = scissor.clip(scissorStack.peek());
        scissorStack.push(scissor);
        scissor.scissor();
    }

    public static void releaseArea() {
        glPopAttrib();
        scissorStack.pop();
    }

    public static double toWindowX(double x) {
        return (double) Minecraft.getMinecraft().displayWidth / getWidth() * x;
    }

    public static double toWindowY(double y) {
        return (double) Minecraft.getMinecraft().displayHeight / getHeight() * y;
    }

    public static double toScaledX(double x) {
        return (double) getWidth() / Minecraft.getMinecraft().displayWidth * x;
    }

    public static double toScaledY(double y) {
        return getHeight() - (double) getHeight() / Minecraft.getMinecraft().displayHeight * y;
    }

    public static void prepare2D() {
        glPushAttrib(GL_ENABLE_BIT | GL_COLOR_BUFFER_BIT);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_ALPHA_TEST);
        glEnable(GL_LINE_SMOOTH);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_CULL_FACE);
    }

    public static void release2D() {
        glPopAttrib();
    }

    public static void setColor(Color color) {
        GlStateManager.color((float) color.getRed() / 255, (float) color.getGreen() / 255, (float) color.getBlue() / 255, (float) color.getAlpha() / 255);
    }

    public static void prepareFont(String font, int pts) {
        prepareText("", font, Color.BLACK, pts, false);
    }

    public static TextPreview prepareText(String text, String font, EspColor color, int pts, boolean shadow) {
        if (font != null) {
            SystemFontTextPreview preview = SystemFontTextPreview.newTextPreview(text, font, color, pts, shadow);
            if (preview != null) return preview;
        }

        return new VanillaTextPreview(text, color, pts, shadow);
    }

    public static TextPreview prepareText(String text, TextSettings settings) {
        return prepareText(text, settings.getFont(), settings.getColor(), settings.getPts(), settings.hasShadow());
    }

    public static TextPreview prepareText(String text, String font, Color color, int pts, boolean shadow) {
        return prepareText(text, font, new EspColor(color, false, true), pts, shadow);
    }

    public static void drawLine(double x1, double y1, double x2, double y2, Color color, double width) {
        prepare2D();

        glLineWidth((float) width);
        setColor(color);
        glBegin(GL_LINES);
        glVertex2d(x1, y1);
        glVertex2d(x2, y2);
        glEnd();

        release2D();
    }

    public static void drawRect(double x, double y, double width, double height, Color color) {
        prepare2D();

        setColor(color);
        glRectd(x, y, x + width, y + height);

        release2D();
    }

    public static void drawOutline(double x, double y, double width, double height, Color color, double pts) {
        prepare2D();

        glDisable(GL_LINE_SMOOTH);
        glLineWidth((float) pts);
        setColor(color);
        glBegin(GL_LINE_LOOP);
        glVertex2d(x, y);
        glVertex2d(x + width, y);
        glVertex2d(x + width, y + height);
        glVertex2d(x, y + height);
        glEnd();

        release2D();
    }

    private static class Scissor {
        final double x;
        final double y;
        final double width;
        final double height;

        Scissor(double x, double y, double width, double height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        Scissor(Component component) {
            x = GuiUtils.toWindowX(component.getWindowX());
            y = Math.max(GuiUtils.getWindowHeight() - GuiUtils.toWindowY(component.getWindowY() + component.getHeight()), 0);
            width = GuiUtils.toWindowX(component.getWidth());
            height = GuiUtils.toWindowY(component.getHeight());
        }

        Scissor clip(Scissor scissor) {
            double newX = Math.max(x, scissor.x);
            double newY = Math.max(y, scissor.y);
            double newWidth = Math.max(Math.min(x + width, scissor.x + scissor.width) - newX, 0);
            double newHeight = Math.max(Math.min(y + height, scissor.y + scissor.height) - newY, 0);
            return new Scissor(newX, newY, newWidth, newHeight);
        }

        void scissor() {
            glScissor((int) x, (int) y, (int) width, (int) height);
        }
    }
}
