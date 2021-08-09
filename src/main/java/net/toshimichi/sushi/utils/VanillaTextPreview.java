package net.toshimichi.sushi.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.toshimichi.sushi.config.data.EspColor;
import net.toshimichi.sushi.utils.render.TextPreview;

import static org.lwjgl.opengl.GL11.*;

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
        return renderer.getStringWidth(text) * pts / 9D;
    }

    @Override
    public double getHeight() {
        return pts;
    }

    @Override
    public void draw(double x, double y) {
        renderer.FONT_HEIGHT = pts;
        glPushMatrix();
        glPushAttrib(GL_COLOR_BUFFER_BIT | GL_ENABLE_BIT);
        glTranslated(x, y, 0);
        glScaled(pts / 9D, pts / 9D, 0);
        glEnable(GL_BLEND);
        if (pts % 9 != 0 && pts < 20) {
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        }
        renderer.drawString(text, 0, 0, color.getColor(y).getRGB(), shadow);
        glPopMatrix();
        glPopAttrib();
    }
}
