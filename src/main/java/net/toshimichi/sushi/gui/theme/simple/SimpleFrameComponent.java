package net.toshimichi.sushi.gui.theme.simple;

import net.toshimichi.sushi.gui.Component;
import net.toshimichi.sushi.gui.Resizable;
import net.toshimichi.sushi.gui.base.EmptyFrameComponent;
import net.toshimichi.sushi.modules.config.Configuration;
import net.toshimichi.sushi.modules.config.Configurations;
import net.toshimichi.sushi.utils.GuiUtils;

import java.awt.Color;

public class SimpleFrameComponent extends EmptyFrameComponent {

    private static final int LINE_WIDTH = 1;
    private static final int FRAME_HEIGHT = 20;
    private final Component component;
    private final Configuration<Color> frameColor;

    public SimpleFrameComponent(Configurations configurations, Component component) {
        super(component);
        this.component = component;
        this.frameColor = configurations.get("gui.frame.color", "Frame Color", Color.class, new Color(200, 90, 30));
    }

    @Override
    public int getWidth() {
        return component.getWidth() + 2 * LINE_WIDTH;
    }

    @Override
    public int getHeight() {
        return component.getHeight() + FRAME_HEIGHT - LINE_WIDTH;
    }

    @Override
    public void setWidth(int width) {
        if (component instanceof Resizable) {
            ((Resizable) component).setWidth(width - 2 * LINE_WIDTH);
        }
    }

    @Override
    public void setHeight(int height) {
        if (component instanceof Resizable) {
            ((Resizable) component).setHeight(height - FRAME_HEIGHT - LINE_WIDTH);
        }
    }

    @Override
    public Component getValue() {
        return component;
    }

    @Override
    public void onRender() {
        GuiUtils.drawRect(getWindowX(), getWindowY(), getWidth(), getHeight(), frameColor.getValue());
        component.onRender();
    }
}
