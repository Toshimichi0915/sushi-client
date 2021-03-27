package net.toshimichi.sushi.gui.base;

import net.toshimichi.sushi.events.input.ClickType;
import net.toshimichi.sushi.gui.*;
import org.lwjgl.input.Keyboard;

public class BaseComponent implements Component {

    private boolean focused;
    private boolean closed;
    private Anchor anchor;
    private Origin origin;
    private Component parent;
    private ComponentContext<?> context;
    private int x;
    private int y;
    private int width;
    private int height;
    private Insets margin = new Insets(0, 0, 0, 0);

    public BaseComponent() {
        this.anchor = Anchor.TOP_LEFT;
        this.origin = Origin.TOP_LEFT;
    }

    public BaseComponent(int x, int y, int width, int height, Anchor anchor, Origin origin, Component parent) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.anchor = anchor;
        this.origin = origin;
        this.parent = parent;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public Insets getMargin() {
        return margin;
    }

    @Override
    public void setMargin(Insets margin) {
        this.margin = margin;
    }

    @Override
    public Anchor getAnchor() {
        return anchor;
    }

    @Override
    public void setAnchor(Anchor anchor) {
        this.anchor = anchor;
    }

    @Override
    public Origin getOrigin() {
        return origin;
    }

    @Override
    public void setOrigin(Origin origin) {
        this.origin = origin;
    }

    @Override
    public Component getParent() {
        return parent;
    }

    @Override
    public void setOrigin(Component origin) {
        this.parent = origin;
    }

    @Override
    public ComponentContext<?> getContext() {
        return context;
    }

    @Override
    public void setContext(ComponentContext<?> context) {
        this.context = context;
    }

    @Override
    public boolean isFocused() {
        return focused;
    }

    @Override
    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public void onRender() {
    }

    @Override
    public void onClick(int x, int y, ClickType type) {
    }

    @Override
    public void onHover(int x, int y) {
    }

    @Override
    public void onHold(int fromX, int fromY, int toX, int toY, ClickType type, MouseStatus status) {
    }

    @Override
    public void onScroll(int deltaX, int deltaY, ClickType type) {
    }

    @Override
    public boolean onKeyPressed(int keyCode, char key) {
        return false;
    }

    @Override
    public boolean onKeyReleased(int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            getContext().close();
            return true;
        }
        return false;
    }

    @Override
    public void onShow() {
    }

    @Override
    public void onClose() {
        closed = true;
    }
}
