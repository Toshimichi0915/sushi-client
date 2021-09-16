package net.sushiclient.client.utils.render;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SystemFont {
    public static final int START_CHAR = ' ';
    public static final int END_CHAR = '~';
    private final int paddingWidth;
    private final int paddingHeight;
    private final int marginWidth;
    private final int marginHeight;
    private final float size;
    private final Font font;
    private final DynamicTexture texture;
    private float fontHeight = -1;
    private final CharData[] chars = new CharData[END_CHAR - START_CHAR + 1];

    public SystemFont(Font font) {
        this.font = font;
        this.paddingWidth = 8;
        this.paddingHeight = 0;
        this.marginWidth = font.getSize() / 10;
        this.marginHeight = font.getSize() / 10;
        this.size = (font.getSize() / 2 + 1) * 32;
        this.texture = newTexture(font, this.chars);
    }

    private DynamicTexture newTexture(Font font, CharData[] chars) {
        BufferedImage img = newFontImage(font, chars);
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ImageIO.write(img, "png", out);
            return new DynamicTexture(img);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private BufferedImage newFontImage(Font font, CharData[] chars) {
        int imageSize = (int) this.size;
        BufferedImage bufferedImage = new BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) bufferedImage.getGraphics();
        g.setFont(font);
        g.setColor(new Color(255, 255, 255, 0));
        g.fillRect(0, 0, imageSize, imageSize);
        g.setColor(Color.WHITE);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        FontMetrics fontMetrics = g.getFontMetrics();
        float maxHeight = 0;
        int positionX = 0;
        int positionY = 1;
        for (int i = 0; i < END_CHAR - START_CHAR + 1; i++) {
            char ch = (char) (i + START_CHAR);
            CharData charData = new CharData();
            Rectangle2D dimensions = fontMetrics.getStringBounds(String.valueOf(ch), g);
            charData.width = (float) dimensions.getBounds2D().getWidth() + paddingWidth;
            charData.height = (float) dimensions.getBounds2D().getHeight() + paddingHeight;
            if (positionX + charData.width >= imageSize) {
                positionX = 0;
                positionY += maxHeight + marginHeight;
                maxHeight = 0;
            }
            if (charData.height > maxHeight) {
                maxHeight = charData.height;
            }
            charData.x = positionX;
            charData.y = positionY;
            if (charData.height > this.fontHeight) {
                this.fontHeight = charData.height;
            }
            chars[i] = charData;
            g.drawString(String.valueOf(ch), positionX + 2, positionY + fontMetrics.getAscent());
            positionX += charData.width + marginWidth;
        }
        return bufferedImage;
    }

    public void drawChar(CharData c, float x, float y) {
        float imgX = c.x / size;
        float imgY = c.y / size;
        float imgWidth = c.width / size;
        float imgHeight = c.height / size;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
        builder.pos(x + c.width, y, 0).tex(imgX + imgWidth, imgY).endVertex();
        builder.pos(x, y, 0).tex(imgX, imgY).endVertex();
        builder.pos(x, y + c.height, 0).tex(imgX, imgY + imgHeight).endVertex();
        builder.pos(x, y + c.height, 0).tex(imgX, imgY + imgHeight).endVertex();
        builder.pos(x + c.width, y + c.height, 0).tex(imgX + imgWidth, imgY + imgHeight).endVertex();
        builder.pos(x + c.width, y, 0).tex(imgX + imgWidth, imgY).endVertex();
        tessellator.draw();
    }

    public CharData getCharData(char c) {
        if (c < START_CHAR || c > END_CHAR) return null;
        return chars[c - START_CHAR];
    }

    public double getStringWidth(String text) {
        double width = 0;
        for (char c : text.toCharArray()) {
            if (c < this.chars.length) {
                width += this.chars[c].width - paddingWidth;
            }
        }
        return width / 2;
    }

    public int getPaddingWidth() {
        return paddingWidth;
    }

    public int getPaddingHeight() {
        return paddingHeight;
    }

    public int getMarginWidth() {
        return marginWidth;
    }

    public int getMarginHeight() {
        return marginHeight;
    }

    public float getSize() {
        return size;
    }

    public Font getFont() {
        return font;
    }

    public DynamicTexture getTexture() {
        return texture;
    }

    public float getFontHeight() {
        return fontHeight;
    }

    public float getHeight() {
        return (this.fontHeight - paddingHeight - 8) / 2;
    }

    public static class CharData {
        public float x;
        public float y;
        public float width;
        public float height;
    }
}