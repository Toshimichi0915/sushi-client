package net.toshimichi.sushi.config.data;

import com.google.gson.annotations.SerializedName;

import java.awt.Color;

public class EspColor {
    @SerializedName("color")
    private final Color color;
    @SerializedName("rainbow")
    private final boolean rainbow;
    @SerializedName("alpha_enabled")
    private final boolean alphaEnabled;

    public EspColor(Color color, boolean rainbow, boolean alphaEnabled) {
        this.color = color;
        this.rainbow = rainbow;
        this.alphaEnabled = alphaEnabled;
    }

    public Color getColor() {
        return color;
    }

    public boolean isRainbow() {
        return rainbow;
    }

    public boolean isAlphaEnabled() {
        return alphaEnabled;
    }

    public Color getCurrentColor() {
        int red, green, blue, alpha;
        if (alphaEnabled) alpha = color.getAlpha();
        else alpha = 255;

        if (rainbow) {
            double h = System.currentTimeMillis() / 10000D - System.currentTimeMillis() / 10000;
            Color color = Color.getHSBColor((float) h, 1, 1);
            red = color.getRed();
            green = color.getGreen();
            blue = color.getBlue();
        } else {
            red = color.getRed();
            green = color.getGreen();
            blue = color.getBlue();
        }
        return new Color(red, green, blue, alpha);
    }

    public EspColor setColor(Color color) {
        return new EspColor(color, rainbow, alphaEnabled);
    }

    public EspColor setRainbow(boolean rainbow) {
        return new EspColor(color, rainbow, alphaEnabled);
    }

    public EspColor setAlphaEnabled(boolean alphaEnabled) {
        return new EspColor(color, rainbow, alphaEnabled);
    }

    public EspColor setAlpha(int alpha) {
        return setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EspColor espColor = (EspColor) o;

        if (rainbow != espColor.rainbow) return false;
        if (alphaEnabled != espColor.alphaEnabled) return false;
        return color != null ? color.equals(espColor.color) : espColor.color == null;
    }

    @Override
    public int hashCode() {
        int result = color != null ? color.hashCode() : 0;
        result = 31 * result + (rainbow ? 1 : 0);
        result = 31 * result + (alphaEnabled ? 1 : 0);
        return result;
    }
}
