package net.toshimichi.sushi.gui;

import net.toshimichi.sushi.events.input.ClickType;
import net.toshimichi.sushi.gui.base.BaseListComponent;
import net.toshimichi.sushi.gui.layout.Layout;
import net.toshimichi.sushi.gui.layout.NullLayout;
import net.toshimichi.sushi.utils.GuiUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Consumer;

public class PanelComponent<T extends Component> extends BaseListComponent<T> {

    private boolean scissorEnabled = true;
    private Layout layout = new NullLayout(this);

    public PanelComponent() {
        super(new ArrayList<>());
    }

    public PanelComponent(int x, int y, int width, int height, Anchor anchor, Component origin) {
        super(x, y, width, height, anchor, origin, new ArrayList<>());
    }

    public T getFocusedComponent() {
        for (T component : this) {
            if (component.isFocused()) return component;
        }
        return null;
    }

    public void setFocusedComponent(T component) {
        forEach(c -> c.setFocused(false));
        component.setFocused(true);
    }

    private void execFocus(Consumer<T> consumer) {
        T focused = getFocusedComponent();
        if (focused != null) {
            consumer.accept(focused);
        }
    }

    private T getTopComponent(int x, int y) {
        for (T child : this) {
            if (child.getWindowX() > x) continue;
            if (child.getWindowX() + child.getWidth() < x) continue;
            if (child.getWindowY() > y) continue;
            if (child.getWindowY() + child.getHeight() < y) continue;
            return child;
        }
        return null;
    }

    public boolean isScissorEnabled() {
        return scissorEnabled;
    }

    public void setScissorEnabled(boolean scissorEnabled) {
        this.scissorEnabled = scissorEnabled;
    }

    @Override
    public void onRender() {
        layout.relocate();
        ArrayList<T> clone = new ArrayList<>(this);
        Collections.reverse(clone);
        for (T component : clone) {
            boolean scissorEnabled = this.scissorEnabled;
            if(scissorEnabled) GuiUtils.prepareArea(component);
            component.onRender();
            if(scissorEnabled) GuiUtils.releaseArea();
        }
    }

    @Override
    public void onClick(int x, int y, ClickType type) {
        T topComponent = getTopComponent(x, y);
        if (topComponent == null) return;
        setFocusedComponent(topComponent);
        topComponent.onClick(x, y, type);
    }

    @Override
    public void onHover(int x, int y) {
        T topComponent = getTopComponent(x, y);
        if (topComponent == null) return;
        topComponent.onHover(x, y);
    }

    @Override
    public void onHold(int fromX, int fromY, int toX, int toY, ClickType type, MouseStatus status) {
        T from = getTopComponent(fromX, fromY);
        T to = getTopComponent(toX, toY);
        if (from == null) return;
        if (status == MouseStatus.END) {
            from.onHold(fromX, fromY, toX, toY, type, status);
            return;
        }
        if (!from.equals(to) && status != MouseStatus.START) {
            from.onHold(fromX, fromY, toX, toY, type, MouseStatus.IN_PROGRESS);
            to = getTopComponent(toX, toY);
            if (!from.equals(to))
                from.onHold(toX, toY, toX, toY, type, MouseStatus.CANCEL);
        }
        if (to == null) return;
        setFocusedComponent(to);
        to.onHold(fromX, fromY, toX, toY, type, status);
    }

    @Override
    public void onScroll(int deltaX, int deltaY, ClickType type) {
        execFocus(c -> c.onScroll(deltaX, deltaY, type));
    }

    @Override
    public void onKeyPressed(int keyCode, char key) {
        execFocus(c -> c.onKeyPressed(keyCode, key));
    }

    @Override
    public void onKeyReleased(int keyCode) {
        super.onKeyReleased(keyCode);
        execFocus(c -> c.onKeyReleased(keyCode));
    }

    @Override
    public boolean add(T component) {
        boolean success = super.add(component);
        layout.relocate();
        return success;
    }

    public Layout getLayout() {
        return layout;
    }

    public void setLayout(Layout layout) {
        this.layout = layout;
    }
}
