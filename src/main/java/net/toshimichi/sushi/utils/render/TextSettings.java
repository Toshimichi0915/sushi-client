package net.toshimichi.sushi.utils.render;

import net.toshimichi.sushi.config.data.EspColor;

public class TextSettings {
    private final String font;
    private final EspColor color;
    private final int pts;
    private final boolean shadow;

    public TextSettings(String font, EspColor color, int pts, boolean shadow) {
        this.font = font;
        this.color = color;
        this.pts = pts;
        this.shadow = shadow;
    }

    public String getFont() {
        return font;
    }

    public EspColor getColor() {
        return color;
    }

    public int getPts() {
        return pts;
    }

    public boolean hasShadow() {
        return shadow;
    }

    public TextSettings setFont(String font) {
        return new TextSettings(font, color, pts, shadow);
    }

    public TextSettings setColor(EspColor color) {
        return new TextSettings(font, color, pts, shadow);
    }

    public TextSettings setPts(int pts) {
        return new TextSettings(font, color, pts, shadow);
    }

    public TextSettings setShadow(boolean shadow) {
        return new TextSettings(font, color, pts, shadow);
    }
}
