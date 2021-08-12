package net.sushiclient.client.modules;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class ResourceImageCategory implements Category {

    private final String name;
    private final Image icon;

    public ResourceImageCategory(String name, String icon) {
        this.name = name;
        this.icon = readImage(icon);
    }

    private static BufferedImage readImage(String resource) {
        try (InputStream in = ResourceImageCategory.class.getResourceAsStream(resource)) {
            if (in == null) return null;
            else return ImageIO.read(in);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
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
