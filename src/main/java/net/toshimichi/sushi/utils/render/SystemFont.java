package net.toshimichi.sushi.utils.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.lwjgl.opengl.GL11.glTexCoord2f;

public class SystemFont {

    protected final int paddingWidth;
    protected final int paddingHeight;
    protected final int marginWidth;
    protected final int marginHeight;
    protected final float imageSize;
    protected final Font font;
    protected final boolean antiAlias;
    protected final boolean fractionalMetrics;
    protected final DynamicTexture tex;
    protected CharData[] charData = new CharData[256];
    protected float fontHeight = -1;
    protected int charOffset = 0;

    public SystemFont(Font font, boolean antiAlias, boolean fractionalMetrics) {
        this.font = font;
        this.paddingWidth = 8;
        this.paddingHeight = 0;
        this.marginWidth = font.getSize();
        this.marginHeight = font.getSize() / 2;
        this.imageSize = (font.getSize() / 20 + 1) * 1024;
        this.antiAlias = antiAlias;
        this.fractionalMetrics = fractionalMetrics;
        this.tex = setupTexture(font, antiAlias, fractionalMetrics, this.charData);
    }

    protected DynamicTexture setupTexture(Font font, boolean antiAlias, boolean fractionalMetrics, CharData[] chars) {
        BufferedImage img = generateFontImage(font, antiAlias, fractionalMetrics, chars);
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ImageIO.write(img, "png", out);
            return new DynamicTexture(img);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected BufferedImage generateFontImage(Font font, boolean antiAlias, boolean fractionalMetrics, CharData[] chars) {
        int imageSize = (int) this.imageSize;
        BufferedImage bufferedImage = new BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) bufferedImage.getGraphics();
        g.setFont(font);
        g.setColor(new Color(255, 255, 255, 0));
        g.fillRect(0, 0, imageSize, imageSize);
        g.setColor(Color.WHITE);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, fractionalMetrics ? RenderingHints.VALUE_FRACTIONALMETRICS_ON : RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antiAlias ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, antiAlias ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        FontMetrics fontMetrics = g.getFontMetrics();
        float charHeight = 0;
        int positionX = 0;
        int positionY = 1;
        for (int i = 0; i < chars.length; i++) {
            char ch = (char) i;
            CharData charData = new CharData();
            Rectangle2D dimensions = fontMetrics.getStringBounds(String.valueOf(ch), g);
            charData.width = (float) dimensions.getBounds2D().getWidth() + paddingWidth;
            charData.height = (float) dimensions.getBounds2D().getHeight() + paddingHeight;
            if (positionX + charData.width >= imageSize) {
                positionX = 0;
                positionY += charHeight + marginHeight;
                charHeight = 0;
            }
            if (charData.height > charHeight) {
                charHeight = charData.height;
            }
            charData.storedX = positionX;
            charData.storedY = positionY;
            if (charData.height > this.fontHeight) {
                this.fontHeight = charData.height;
            }
            chars[i] = charData;
            g.drawString(String.valueOf(ch), positionX + 2, positionY + fontMetrics.getAscent());
            positionX += charData.width + marginWidth;
        }
        return bufferedImage;
    }

    public void drawChar(CharData[] chars, char c, float x, float y) {
        drawQuad(x, y, chars[c].width, chars[c].height, chars[c].storedX, chars[c].storedY, chars[c].width, chars[c].height);
    }

    protected void drawQuad(float x, float y, float width, float height, float srcX, float srcY, float srcWidth, float srcHeight) {
        float renderSRCX = srcX / imageSize;
        float renderSRCY = srcY / imageSize;
        float renderSRCWidth = srcWidth / imageSize;
        float renderSRCHeight = srcHeight / imageSize;
        glTexCoord2f(renderSRCX + renderSRCWidth, renderSRCY);
        GlStateManager.glVertex3f(x + width, (float) y, 0);
        glTexCoord2f(renderSRCX, renderSRCY);
        GlStateManager.glVertex3f(x, y, 0);
        glTexCoord2f(renderSRCX, renderSRCY + renderSRCHeight);
        GlStateManager.glVertex3f(x, y + height, 0);
        glTexCoord2f(renderSRCX, renderSRCY + renderSRCHeight);
        GlStateManager.glVertex3f(x, y + height, 0);
        glTexCoord2f(renderSRCX + renderSRCWidth, renderSRCY + renderSRCHeight);
        GlStateManager.glVertex3f(x + width, y + height, 0);
        glTexCoord2f(renderSRCX + renderSRCWidth, renderSRCY);
        GlStateManager.glVertex3f(x + width, y, 0);
    }

    public float getHeight() {
        return (this.fontHeight - paddingHeight - 8) / 2;
    }

    public double getStringWidth(String text) {
        double width = 0;
        for (char c : text.toCharArray()) {
            if (c < this.charData.length) {
                width += this.charData[c].width - paddingWidth + this.charOffset;
            }
        }
        return width / 2;
    }

    protected static class CharData {
        public float width;
        public float height;
        public float storedX;
        public float storedY;
    }
}