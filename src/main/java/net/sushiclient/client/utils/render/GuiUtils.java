package net.sushiclient.client.utils.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.sushiclient.client.config.data.EspColor;
import net.sushiclient.client.gui.Component;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.util.Stack;

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
            Minecraft.getMinecraft().displayGuiScreen(((LockGuiScreen) screen).getParent());
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
        GL11.glPushAttrib(GL11.GL_SCISSOR_BIT);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        Scissor scissor = new Scissor(component);
        if (!scissorStack.isEmpty())
            scissor = scissor.clip(scissorStack.peek());
        scissorStack.push(scissor);
        scissor.scissor();
    }

    public static void prepareArea(double x, double y, double width, double height) {
        GL11.glPushAttrib(GL11.GL_SCISSOR_BIT);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        y = Math.max(GuiUtils.getWindowHeight() - GuiUtils.toWindowY(y + height), 0);
        Scissor scissor = new Scissor(GuiUtils.toWindowX(x), y, GuiUtils.toWindowX(width), GuiUtils.toWindowY(height));
        if (!scissorStack.isEmpty())
            scissor = scissor.clip(scissorStack.peek());
        scissorStack.push(scissor);
        scissor.scissor();
    }

    public static void releaseArea() {
        GL11.glPopAttrib();
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
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableAlpha();
        GlStateManager.disableTexture2D();
        GlStateManager.disableCull();
    }

    public static void release2D() {
        GlStateManager.enableTexture2D();
    }

    public static void setColor(Color color) {
        GlStateManager.color((float) color.getRed() / 255, (float) color.getGreen() / 255, (float) color.getBlue() / 255, (float) color.getAlpha() / 255);
    }

    public static void prepareFont(String font, int pts) {
        // 100 mega bytes
        if (Runtime.getRuntime().freeMemory() < 100_000_000) return;
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

    public static void drawLine(double x1, double y1, double x2, double y2, Color color, double width, boolean antialias) {
        prepare2D();
        if (antialias) {
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
        }

        GlStateManager.glLineWidth((float) width);
        setColor(color);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
        builder.pos(x1, y1, 0).endVertex();
        builder.pos(x2, y2, 0).endVertex();
        tessellator.draw();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);

        release2D();
    }

    private static void drawRect(int mode, double x, double y, double width, double height, Color color) {
        prepare2D();

        setColor(color);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(mode, DefaultVertexFormats.POSITION);
        builder.pos(x, y, 0).endVertex();
        builder.pos(x + width, y, 0).endVertex();
        builder.pos(x + width, y + height, 0).endVertex();
        builder.pos(x, y + height, 0).endVertex();
        tessellator.draw();

        release2D();
    }

    public static void drawRect(double x, double y, double width, double height, Color color) {
        drawRect(GL11.GL_QUADS, x, y, width, height, color);
    }

    public static void drawOutline(double x, double y, double width, double height, Color color, double pts) {
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GlStateManager.glLineWidth((float) pts);
        drawRect(GL11.GL_LINE_LOOP, x, y, width, height, color);
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
            GL11.glScissor((int) x, (int) y, (int) width, (int) height);
        }
    }
}
