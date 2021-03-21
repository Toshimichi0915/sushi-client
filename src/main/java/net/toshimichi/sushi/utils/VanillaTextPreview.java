package net.toshimichi.sushi.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import java.awt.Color;

public class VanillaTextPreview implements TextPreview {

    private final FontRenderer renderer;
    private final String text;
    private final Color color;
    private final int pts;
    private final boolean shadow;

    public VanillaTextPreview(String text, Color color, int pts, boolean shadow) {
        this.renderer = Minecraft.getMinecraft().fontRenderer;
        this.text = text;
        this.color = color;
        this.pts = pts;
        this.shadow = shadow;
    }

    @Override
    public int getWidth() {
        return renderer.getStringWidth(text);
    }

    @Override
    public int getHeight() {
        return renderer.FONT_HEIGHT;
    }

    @Override
    public void draw(int x, int y) {
        GuiUtils.setColor(color);
        renderer.FONT_HEIGHT = pts;
        renderer.drawString(text, x, y, color.getRGB(), shadow);
    }
}