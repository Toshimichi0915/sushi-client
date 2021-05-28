package net.toshimichi.sushi.utils;

import net.toshimichi.sushi.config.data.EspColor;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;

public class SystemFontTextPreview implements TextPreview {

    private static final ArrayList<FontData> cachedFonts = new ArrayList<>();

    private final String text;
    private final SystemFontRenderer renderer;
    private final boolean shadow;
    private EspColor color;

    public SystemFontTextPreview(String text, SystemFontRenderer renderer, EspColor color, boolean shadow) {
        this.text = text;
        this.renderer = renderer;
        this.color = color;
        this.shadow = shadow;
    }

    @Override
    public double getWidth() {
        return renderer.getStringWidth(text);
    }

    @Override
    public double getHeight() {
        return renderer.getHeight();
    }

    @Override
    public void draw(double x, double y) {
        if (color.isRainbow()) {
            double h = System.currentTimeMillis() / 10000D - System.currentTimeMillis() / 10000;
            color = color.setColor(Color.getHSBColor((float) (y / GuiUtils.getWindowHeight() + h), 1, 1));
        }
        if (shadow) {
            renderer.drawString(text, x + 0.5F, y + 2.5F, new Color(100, 100, 100, color.getColor().getAlpha()), false);
        }
        renderer.drawString(text, x, y + 2, color.getCurrentColor(), false);
    }

    public static SystemFontTextPreview newTextPreview(String text, String font, EspColor color, int pts, boolean shadow) {
        // load from cached fonts
        for (FontData ttf : cachedFonts) {
            if (ttf.fontName.equals(font) && ttf.size == pts) {
                return new SystemFontTextPreview(text, ttf.font, color, shadow);
            }
        }

        // check if the font exists
        boolean fontFound = false;
        for (String s : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
            if (s.equals(font)) {
                fontFound = true;
                break;
            }
        }
        if (!fontFound)
            return null;

        // load TTF
        SystemFontRenderer renderer = new SystemFontRenderer(new Font(font, Font.PLAIN, pts * 2), true, true);
        cachedFonts.add(new FontData(font, renderer, pts));
        return new SystemFontTextPreview(text, renderer, color, shadow);
    }

    private static class FontData {
        String fontName;
        SystemFontRenderer font;
        int size;

        FontData(String fontName, SystemFontRenderer font, int size) {
            this.fontName = fontName;
            this.font = font;
            this.size = size;
        }
    }
}
