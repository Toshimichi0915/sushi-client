package net.toshimichi.sushi.utils.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.awt.Font;

public class SystemFontRenderer extends SystemFont {

    protected final CharData[] boldChars = new CharData[256];
    protected final CharData[] italicChars = new CharData[256];
    protected final CharData[] boldItalicChars = new CharData[256];
    protected DynamicTexture texBold;
    protected DynamicTexture texItalic;
    protected DynamicTexture texItalicBold;

    private final int[] colorCodes = new int[32];

    public SystemFontRenderer(Font font, boolean antiAlias, boolean fractionalMetrics) {
        super(font, antiAlias, fractionalMetrics);
        setUpColorCodes();
        setUpDecoratedChars();
    }

    public void drawString(String text, double x, double y, Color color, boolean shadow) {
        x -= 1;
        y -= 2;
        if (text == null) return;
        if (color.getRed() == 255 && color.getGreen() == 255 && color.getBlue() == 255 && color.getAlpha() == 32)
            color = new Color(255, 255, 255);
        if (color.getAlpha() < 4)
            color = new Color(color.getRed(), color.getGreen(), color.getBlue(), 255);
        if (shadow)
            color = new Color(color.getRed() / 4, color.getGreen() / 4, color.getBlue() / 4, color.getAlpha());

        CharData[] currentData = this.charData;
        boolean randomCase = false;
        boolean bold = false;
        boolean italic = false;
        boolean strikethrough = false;
        boolean underline = false;
        x *= 2.0D;
        y *= 2.0D;
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.5D, 0.5D, 0.5D);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
        GlStateManager.enableTexture2D();
        GlStateManager.bindTexture(tex.getGlTextureId());
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\u00A7') {
                int colorIndex = 21;
                try {
                    colorIndex = "0123456789abcdefklmnor".indexOf(text.charAt(i + 1));
                } catch (IndexOutOfBoundsException e) {
                    // invalid color code
                }
                if (colorIndex < 16) {
                    bold = false;
                    italic = false;
                    randomCase = false;
                    underline = false;
                    strikethrough = false;
                    GlStateManager.bindTexture(tex.getGlTextureId());
                    currentData = this.charData;
                    if (colorIndex < 0) colorIndex = 15;
                    if (shadow) colorIndex += 16;
                    int colorCode = this.colorCodes[colorIndex];
                    GlStateManager.color((colorCode >> 16 & 0xFF) / 255.0F, (colorCode >> 8 & 0xFF) / 255.0F, (colorCode & 0xFF) / 255.0F, color.getAlpha());
                } else if (colorIndex == 16) {
                    randomCase = true;
                    // not implemented
                } else if (colorIndex == 17) {
                    bold = true;
                    if (italic) {
                        GlStateManager.bindTexture(texItalicBold.getGlTextureId());
                        currentData = this.boldItalicChars;
                    } else {
                        GlStateManager.bindTexture(texBold.getGlTextureId());
                        currentData = this.boldChars;
                    }
                } else if (colorIndex == 18) {
                    strikethrough = true;
                } else if (colorIndex == 19) {
                    underline = true;
                } else if (colorIndex == 20) {
                    italic = true;
                    if (bold) {
                        GlStateManager.bindTexture(texItalicBold.getGlTextureId());
                        currentData = this.boldItalicChars;
                    } else {
                        GlStateManager.bindTexture(texItalic.getGlTextureId());
                        currentData = this.italicChars;
                    }
                } else {
                    bold = false;
                    italic = false;
                    randomCase = false;
                    underline = false;
                    strikethrough = false;
                    GlStateManager.color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
                    GlStateManager.bindTexture(tex.getGlTextureId());
                    currentData = this.charData;
                }
                i++;
            } else if (c < currentData.length) {
                GlStateManager.glBegin(GL11.GL_TRIANGLES);
                drawChar(currentData, c, (float) x, (float) y);
                GlStateManager.glEnd();
                if (strikethrough)
                    drawLine(x, y + (double) currentData[c].height / 2, x + currentData[c].width - 8.0D, y + (double) currentData[c].height / 2, 1.0F);
                if (underline)
                    drawLine(x, y + currentData[c].height - 2.0D, x + currentData[c].width - 8.0D, y + currentData[c].height - 2.0D, 1.0F);
                x += currentData[c].width - 8 + this.charOffset;
            }
        }
        GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_DONT_CARE);
        GlStateManager.popMatrix();
    }

    @Override
    public int getStringWidth(String text) {
        if (text == null) {
            return 0;
        }
        int width = 0;
        CharData[] currentData = this.charData;
        boolean bold = false;
        boolean italic = false;
        int size = text.length();

        for (int i = 0; i < size; i++) {
            char character = text.charAt(i);
            if (character == '\u00A7') {
                int colorIndex = 21;
                try {
                    colorIndex = "0123456789abcdefklmnor".indexOf(text.charAt(i + 1));
                } catch (IndexOutOfBoundsException e) {
                    // skip
                }
                if (colorIndex < 16) {
                    bold = false;
                    italic = false;
                } else if (colorIndex == 17) {
                    bold = true;
                    if (italic) {
                        currentData = this.boldItalicChars;
                    } else {
                        currentData = this.boldChars;
                    }
                } else if (colorIndex == 20) {
                    italic = true;
                    if (bold) {
                        currentData = this.boldItalicChars;
                    } else {
                        currentData = this.italicChars;
                    }
                } else if (colorIndex == 21) {
                    bold = false;
                    italic = false;
                    currentData = this.charData;
                }
                i++;
            } else if (character < currentData.length) {
                width += currentData[character].width - 8 + this.charOffset;
            }
        }
        return width / 2;
    }


    private void drawLine(double x, double y, double x1, double y1, float width) {
        GlStateManager.disableTexture2D();
        GlStateManager.glLineWidth(width);
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x1, y1);
        GL11.glEnd();
        GlStateManager.enableTexture2D();
    }

    private void setUpDecoratedChars() {
        this.texBold = setupTexture(this.font.deriveFont(Font.BOLD), this.antiAlias, this.fractionalMetrics, this.boldChars);
        this.texItalic = setupTexture(this.font.deriveFont(Font.ITALIC), this.antiAlias, this.fractionalMetrics, this.italicChars);
        this.texItalicBold = setupTexture(this.font.deriveFont(Font.BOLD | Font.ITALIC), this.antiAlias, this.fractionalMetrics, this.boldItalicChars);
    }

    private void setUpColorCodes() {
        for (int index = 0; index < 32; index++) {
            int noClue = (index >> 3 & 0x1) * 85;
            int red = (index >> 2 & 0x1) * 170 + noClue;
            int green = (index >> 1 & 0x1) * 170 + noClue;
            int blue = (index >> 0 & 0x1) * 170 + noClue;

            if (index == 6) {
                red += 85;
            }
            if (index >= 16) {
                red /= 4;
                green /= 4;
                blue /= 4;
            }
            this.colorCodes[index] = ((red & 0xFF) << 16 | (green & 0xFF) << 8 | blue & 0xFF);
        }
    }
}