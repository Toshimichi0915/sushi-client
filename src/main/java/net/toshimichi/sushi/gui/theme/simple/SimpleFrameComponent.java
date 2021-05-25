package net.toshimichi.sushi.gui.theme.simple;

import net.toshimichi.sushi.events.input.ClickType;
import net.toshimichi.sushi.gui.Component;
import net.toshimichi.sushi.gui.FrameComponent;
import net.toshimichi.sushi.gui.Insets;
import net.toshimichi.sushi.gui.MouseStatus;
import net.toshimichi.sushi.gui.base.BasePanelComponent;
import net.toshimichi.sushi.gui.layout.Layout;
import net.toshimichi.sushi.gui.theme.ThemeConstants;
import net.toshimichi.sushi.utils.GuiUtils;

import java.awt.Color;

public class SimpleFrameComponent<T extends Component> extends BasePanelComponent<T> implements FrameComponent<T>, Layout {

    private static final double MARGIN = 2;
    private static final double BAR_WIDTH = 15;
    private static final double BAR_HEIGHT = 8;
    private final ThemeConstants constants;
    private final T component;
    private int holdX;
    private int holdY;
    private boolean hover;
    private boolean hold;

    public SimpleFrameComponent(ThemeConstants constants, T component) {
        this.constants = constants;
        this.component = component;
        add(component);
        setLayout(this);
    }

    @Override
    public T getValue() {
        return component;
    }

    @Override
    public void onRender() {
//        Color color = constants.crossMarkColor.getValue();
        Color backgroundColor = constants.crossMarkBackgroundColor.getValue();
        if (hover) {
//            color = constants.hoverCrossMarkColor.getValue();
            backgroundColor = constants.hoverCrossMarkBackgroundColor.getValue();
        }
        if (hold) {
//            color = constants.selectedCrossMarkColor.getValue();
            backgroundColor = constants.selectedCrossMarkBackgroundColor.getValue();
        }
        hover = false;
        hold = false;
        GuiUtils.drawRect(component.getWindowX() - MARGIN, component.getWindowY() - MARGIN, component.getWidth() + 2 * MARGIN, component.getHeight() + 2 * MARGIN, constants.menuBarColor.getValue());
        GuiUtils.drawRect(getWindowX(), getWindowY(), getWidth(), BAR_HEIGHT + 2 * MARGIN, constants.menuBarColor.getValue());
        GuiUtils.drawRect(getWindowX() + getWidth() - BAR_WIDTH - MARGIN, getWindowY() + MARGIN, BAR_WIDTH, BAR_HEIGHT, backgroundColor);
//        GuiUtils.drawLine(getWindowX() + getWidth() - BAR_WIDTH + 6, getWindowY() + 4, getWindowX() + getWidth() - 6, getWindowY() + BAR_HEIGHT - 4, color, 2);
//        GuiUtils.drawLine(getWindowX() + getWidth() - BAR_WIDTH + 6, getWindowY() + BAR_HEIGHT - 4, getWindowX() + getWidth() - 6, getWindowY() + 4, color, 2);
        super.onRender();
    }

    private boolean isMenuBar(int x, int y) {
        return getWindowX() <= x &&
                getWindowY() <= y &&
                x <= getWindowX() + getWidth() &&
                y <= getWindowY() + BAR_HEIGHT + 2 * MARGIN;
    }

    private boolean isCrossMark(int x, int y) {
        return getWindowX() + getWidth() - BAR_WIDTH - MARGIN <= x &&
                getWindowY() + MARGIN <= y &&
                x <= getWindowX() + getWidth() - MARGIN &&
                y <= getWindowY() + BAR_HEIGHT + MARGIN;
    }

    @Override
    public void onHover(int x, int y) {
        if (isCrossMark(x, y)) {
            hover = true;
        }
        super.onHover(x, y);
    }

    @Override
    public void onClick(int x, int y, ClickType type) {
        if (isCrossMark(x, y)) {
            getContext().close();
        } else {
            super.onClick(x, y, type);
        }
    }

    @Override
    public void onHold(int fromX, int fromY, int toX, int toY, ClickType type, MouseStatus status) {
        if (type != ClickType.LEFT) return;
        if (isCrossMark(fromX, fromY)) {
            hold = true;
            if (status == MouseStatus.END) {
                getContext().close();
            }
        } else if (isMenuBar(fromX, fromY)) {
            if (status == MouseStatus.START) {
                this.holdX = (int) (fromX - getWindowX());
                this.holdY = (int) (fromY - getWindowY());
                return;
            }
            setWindowX(toX - holdX);
            setWindowY(toY - holdY);
        } else {
            super.onHold(fromX, fromY, toX, toY, type, status);
        }
    }

    @Override
    public void relocate() {
        component.setParent(this);
        component.setX(MARGIN);
        component.setY(BAR_HEIGHT + 2 * MARGIN);
        component.setWidth(getWidth() - 2 * MARGIN);
        component.setHeight(getHeight() - 2 * MARGIN);
        component.onRelocate();
    }

    @Override
    public Insets getFrame() {
        return new Insets(BAR_HEIGHT + 2 * MARGIN, MARGIN, MARGIN, MARGIN);
    }
}
