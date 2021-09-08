package net.sushiclient.client.utils.render;

import net.sushiclient.client.config.data.EspColor;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;
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
        if (shadow) {
            renderer.drawString(text, x + 0.5F, y + 2.5F, color.getColor(y), true);
        }
        renderer.drawString(text, x, y + 2, color.getColor(y), false);
    }

    public static SystemFontTextPreview newTextPreview(String text, String fontName, EspColor color, int pts, boolean shadow) {
        // load from cached fonts
        for (FontData ttf : cachedFonts) {
            if (ttf.fontName.equals(fontName) && ttf.size == pts) {
                return new SystemFontTextPreview(text, ttf.font, color, shadow);
            }
        }

        // check if the font exists
        Font font = null;
        for (String s : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
            if (s.equals(fontName)) {
                font = new Font(fontName, Font.PLAIN, pts * 2);
                break;
            }
        }
        if (font == null) {
            try (InputStream in = SystemFontTextPreview.class.getResourceAsStream("/assets/minecraft/sushi/font/" + fontName + ".ttf")) {
                if (in != null) font = Font.createFont(Font.TRUETYPE_FONT, in).deriveFont(Font.PLAIN, pts * 2);
            } catch (IOException | FontFormatException e) {
                return null;
            }
        }
        if (font == null) return null;

        // load TTF
        SystemFontRenderer renderer = new SystemFontRenderer(font);
        cachedFonts.add(new FontData(fontName, renderer, pts));
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
