package net.toshimichi.sushi.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.toshimichi.sushi.config.data.EspColor;

import java.awt.Color;

public class VanillaTextPreview implements TextPreview {

    private final FontRenderer renderer;
    private final String text;
    private final int pts;
    private final boolean shadow;
    private EspColor color;

    public VanillaTextPreview(String text, EspColor color, int pts, boolean shadow) {
        this.renderer = Minecraft.getMinecraft().fontRenderer;
        this.text = text;
        this.color = color;
        this.pts = pts;
        this.shadow = shadow;
    }

    @Override
    public double getWidth() {
        return renderer.getStringWidth(text);
    }

    @Override
    public double getHeight() {
        return renderer.FONT_HEIGHT;
    }

    @Override
    public void draw(double x, double y) {
        renderer.FONT_HEIGHT = pts;
        if (color.isRainbow()) {
            double h = System.currentTimeMillis() / 10000D - System.currentTimeMillis() / 10000;
            color = color.setColor(Color.getHSBColor((float) (y / GuiUtils.getWindowHeight() + h), 1, 1));
        }
        renderer.drawString(text, (int) x, (int) y, color.getColor().getRGB(), shadow);
    }
}
