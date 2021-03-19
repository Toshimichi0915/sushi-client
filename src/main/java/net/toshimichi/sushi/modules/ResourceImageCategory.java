package net.toshimichi.sushi.modules;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class ResourceImageCategory implements Category {

    private static final BufferedImage emptyImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
    private final String name;
    private final Image icon;

    public ResourceImageCategory(String name, String icon) {
        this.name = name;
        this.icon = readImage(icon);
    }

    private static BufferedImage readImage(String resource) {
        try (InputStream in = ResourceImageCategory.class.getResourceAsStream(resource)) {
            if (in == null) return emptyImage;
            else return ImageIO.read(in);
        } catch (IOException e) {
            e.printStackTrace();
            return emptyImage;
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Image getIcon() {
        return icon;
    }
}
