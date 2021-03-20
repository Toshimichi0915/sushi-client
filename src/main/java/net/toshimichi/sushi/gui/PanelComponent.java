package net.toshimichi.sushi.gui;

import net.toshimichi.sushi.events.input.ClickType;
import net.toshimichi.sushi.gui.base.BaseListComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Function;

public class PanelComponent extends BaseListComponent<Component> {

    private final Function<Component, FrameComponent> frameFunction;

    public PanelComponent() {
        this(null);
    }

    public PanelComponent(Function<Component, FrameComponent> frameFunction) {
        super(new ArrayList<>());
        this.frameFunction = frameFunction;
    }

    public PanelComponent(int x, int y, int width, int height, Anchor anchor, Component origin, Function<Component, FrameComponent> frameFunction) {
        super(x, y, width, height, anchor, origin, new ArrayList<>());
        this.frameFunction = frameFunction;
    }

    private Component getFocusedComponent() {
        for (Component component : this) {
            if (component.isFocused()) return component;
        }
        return null;
    }

    private void execFocus(Consumer<Component> consumer) {
        Component focused = getFocusedComponent();
        if (focused != null) {
            consumer.accept(focused);
        }
    }

    private void setFocusedComponent(Component component) {
        forEach(c -> c.setFocused(false));
        component.setFocused(true);
    }

    private Component getTopComponent(int x, int y) {
        for (Component child : this) {
            if (!child.isVisible()) continue;
            if (child.getWindowX() > x) continue;
            if (child.getWindowX() + child.getWidth() < x) continue;
            if (child.getWindowY() > y) continue;
            if (child.getWindowY() + child.getHeight() < y) continue;
            return child;
        }
        return null;
    }

    @Override
    public void onRender() {
        ArrayList<Component> clone = new ArrayList<>(this);
        Collections.reverse(clone);
        for (Component component : clone) {
            component.onRender();
        }
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        for (Component child : this) {
            child.setVisible(true);
        }
    }

    @Override
    public void onClick(int x, int y, ClickType type) {
        Component topComponent = getTopComponent(x, y);
        if (topComponent == null) return;
        setFocusedComponent(topComponent);
        topComponent.onClick(x, y, type);
    }

    @Override
    public void onHold(int fromX, int fromY, int toX, int toY, ClickType type, MouseStatus status) {
        Component from = getTopComponent(fromX, fromY);
        Component to = getTopComponent(toX, toY);
        if (from == null) return;
        if (status == MouseStatus.END) {
            from.onHold(fromX, fromY, toX, toY, type, status);
            return;
        }
        if (!from.equals(to) && status != MouseStatus.START) {
            from.onHold(fromX, fromY, toX, toY, type, MouseStatus.CANCEL);
        }
        if (to == null) return;
        to.onHold(fromX, fromY, toX, toY, type, status);
    }

    @Override
    public void onScroll(int deltaX, int deltaY, ClickType type) {
        execFocus(c -> c.onScroll(deltaX, deltaY, type));
    }

    @Override
    public void onKeyPressed(int keyCode) {
        execFocus(c -> c.onKeyPressed(keyCode));
    }

    @Override
    public void onKeyReleased(int keyCode) {
        super.onKeyReleased(keyCode);
        execFocus(c -> c.onKeyPressed(keyCode));
    }

    @Override
    public boolean add(Component component) {
        if (frameFunction != null)
            component = frameFunction.apply(component);
        int windowX = component.getWindowX();
        int windowY = component.getWindowY();
        component.setOrigin(this);
        component.setWindowX(windowX);
        component.setWindowY(windowY);
        return super.add(component);
    }
}
