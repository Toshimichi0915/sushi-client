package net.toshimichi.sushi.gui;

import net.toshimichi.sushi.events.input.ClickType;
import net.toshimichi.sushi.utils.GuiUtils;

public interface Component {
    int getX();

    int getY();

    void setX(int x);

    void setY(int y);

    int getWidth();

    int getHeight();

    void setWidth(int width);

    void setHeight(int height);

    Insets getMargin();

    void setMargin(Insets margin);

    default int getWindowX() {
        if (getOrigin() == null)
            return (int) (getX() + GuiUtils.getWidth() * getAnchor().getX());
        return (int) (getX() + getOrigin().getWindowX() + getOrigin().getWidth() * getAnchor().getX());
    }

    default int getWindowY() {
        if (getOrigin() == null)
            return (int) (getY() + GuiUtils.getHeight() * getAnchor().getY());
        return (int) (getY() + getOrigin().getWindowY() + getOrigin().getHeight() * getAnchor().getY());
    }

    default void setWindowX(int x) {
        if (getOrigin() == null)
            setX(x);
        else
            setX((int) (x - getOrigin().getWindowX() - getOrigin().getWidth() * getAnchor().getX()));
    }

    default void setWindowY(int y) {
        if (getOrigin() == null)
            setY(y);
        else
            setY((int) (y - getOrigin().getWindowY() - getOrigin().getHeight() * getAnchor().getY()));
    }

    Anchor getAnchor();

    void setAnchor(Anchor anchor);

    Component getOrigin();

    void setOrigin(Component component);

    ComponentContext<?> getContext();

    void setContext(ComponentContext<?> context);

    boolean isFocused();

    void setFocused(boolean focused);

    boolean isClosed();

    void onRender();

    void onClick(int x, int y, ClickType type);

    void onHover(int x, int y);

    void onHold(int fromX, int fromY, int toX, int toY, ClickType type, MouseStatus status);

    void onScroll(int deltaX, int deltaY, ClickType type);

    boolean onKeyPressed(int keyCode, char key);

    boolean onKeyReleased(int keyCode);

    void onShow();

    void onClose();
}
