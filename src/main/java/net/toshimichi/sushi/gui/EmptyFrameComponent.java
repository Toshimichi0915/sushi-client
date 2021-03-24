package net.toshimichi.sushi.gui;

import net.toshimichi.sushi.events.input.ClickType;
import net.toshimichi.sushi.gui.base.BaseFrameComponent;

public class EmptyFrameComponent extends BaseFrameComponent {

    private final Component component;

    public EmptyFrameComponent(Component component) {
        this.component = component;
    }

    @Override
    public Component getValue() {
        return component;
    }

    @Override
    public int getX() {
        return component.getX();
    }

    @Override
    public int getY() {
        return component.getY();
    }

    @Override
    public void setX(int x) {
        component.setX(x);
    }

    @Override
    public void setY(int y) {
        component.setY(y);
    }

    @Override
    public int getWidth() {
        return component.getWidth();
    }

    @Override
    public int getHeight() {
        return component.getHeight();
    }

    @Override
    public void setWidth(int width) {
        component.setWidth(width);
    }

    @Override
    public void setHeight(int height) {
        component.setHeight(height);
    }

    @Override
    public int getWindowX() {
        return component.getWindowX();
    }

    @Override
    public int getWindowY() {
        return component.getWindowY();
    }

    @Override
    public void setWindowX(int x) {
        component.setWindowX(x);
    }

    @Override
    public void setWindowY(int y) {
        component.setWindowY(y);
    }

    @Override
    public Anchor getAnchor() {
        return component.getAnchor();
    }

    @Override
    public void setAnchor(Anchor anchor) {
        component.setAnchor(anchor);
    }

    @Override
    public Component getOrigin() {
        return component.getOrigin();
    }

    @Override
    public void setOrigin(Component component) {
        this.component.setOrigin(component);
    }

    @Override
    public boolean isFocused() {
        return component.isFocused();
    }

    @Override
    public void setFocused(boolean focused) {
        component.setFocused(focused);
    }

    @Override
    public boolean isVisible() {
        return component.isVisible();
    }

    @Override
    public void setVisible(boolean visible) {
        component.setVisible(visible);
    }

    @Override
    public void onClick(int x, int y, ClickType type) {
        component.onClick(x, y, type);
    }

    @Override
    public void onHold(int fromX, int fromY, int toX, int toY, ClickType type, MouseStatus status) {
        component.onHold(fromX, fromY, toX, toY, type, status);
    }

    @Override
    public void onScroll(int deltaX, int deltaY, ClickType type) {
        component.onScroll(deltaX, deltaY, type);
    }

    @Override
    public void onKeyPressed(int keyCode) {
        component.onKeyPressed(keyCode);
    }

    @Override
    public void onKeyReleased(int keyCode) {
        component.onKeyReleased(keyCode);
    }

    @Override
    public void onRender() {
        component.onRender();
    }
}
