package net.toshimichi.sushi.config.data;

import java.awt.Color;

public class EspColor {
    private final Color color;
    private final boolean rainbow;
    private final boolean alphaEnabled;

    public EspColor(Color color, boolean alphaEnabled) {
        this.color = color;
        this.rainbow = false;
        this.alphaEnabled = alphaEnabled;
    }

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
        return new EspColor(color, alphaEnabled);
    }

    public EspColor setRainbow(boolean rainbow) {
        return new EspColor(color, rainbow, alphaEnabled);
    }

    public EspColor setAlphaEnabled(boolean alphaEnabled) {
        return new EspColor(color, alphaEnabled);
    }

    public EspColor setAlpha(int alpha) {
        return new EspColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha), alphaEnabled);
    }
}
