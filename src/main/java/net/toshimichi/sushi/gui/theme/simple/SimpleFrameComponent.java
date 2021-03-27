package net.toshimichi.sushi.gui.theme.simple;

import net.toshimichi.sushi.gui.Component;
import net.toshimichi.sushi.gui.EmptyFrameComponent;
import net.toshimichi.sushi.gui.theme.ThemeConstants;
import net.toshimichi.sushi.utils.GuiUtils;

public class SimpleFrameComponent<T extends Component> extends EmptyFrameComponent<T> {

    private static final int LINE_WIDTH = 1;
    private static final int FRAME_HEIGHT = 20;
    private final T component;
    private final ThemeConstants constants;

    public SimpleFrameComponent(ThemeConstants constants, T component) {
        super(component);
        this.component = component;
        this.constants = constants;
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
        component.setWidth(width - 2 * LINE_WIDTH);
    }

    @Override
    public void setHeight(int height) {
        component.setHeight(height - FRAME_HEIGHT - LINE_WIDTH);
    }

    @Override
    public T getValue() {
        return component;
    }

    @Override
    public void onRender() {
        GuiUtils.drawRect(getWindowX(), getWindowY(), getWidth(), getHeight(), constants.frameColor.getValue());
        component.onRender();
    }
}
