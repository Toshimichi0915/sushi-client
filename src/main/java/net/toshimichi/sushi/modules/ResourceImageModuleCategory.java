package net.toshimichi.sushi.modules;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class ResourceModuleCategory implements ModuleCategory {

    private final String name;
    private final Image icon;

    public ResourceModuleCategory(String name, String icon) {
        this.name = name;
        this.icon = readImage(icon);
    }

    private static BufferedImage readImage(String resource) {
        try (InputStream in = ResourceModuleCategory.class.getResourceAsStream(resource)) {
            return ImageIO.read(in);
        } catch (IOException e) {
            e.printStackTrace();
            return new BufferedImage(0, 0, BufferedImage.TYPE_INT_RGB);
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
