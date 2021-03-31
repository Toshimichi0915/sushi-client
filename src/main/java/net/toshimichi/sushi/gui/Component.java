package net.toshimichi.sushi.gui;

import net.toshimichi.sushi.events.input.ClickType;
import net.toshimichi.sushi.utils.GuiUtils;

import java.util.List;

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
        int deltaX = getOrigin().isFromRight() ? getWidth() : 0;
        if (getParent() == null)
            return (int) (getX() - deltaX + GuiUtils.getWidth() * getAnchor().getX());
        return (int) (getX() - deltaX + getParent().getWindowX() + getParent().getWidth() * getAnchor().getX());
    }

    default int getWindowY() {
        int deltaY = getOrigin().isFromBottom() ? getHeight() : 0;
        if (getParent() == null)
            return (int) (getY() - deltaY + GuiUtils.getHeight() * getAnchor().getY());
        return (int) (getY() - deltaY + getParent().getWindowY() + getParent().getHeight() * getAnchor().getY());
    }

    default void setWindowX(int x) {
        int deltaX = getOrigin().isFromRight() ? getWidth() : 0;
        if (getParent() == null)
            setX((int) (x + deltaX - GuiUtils.getWidth() * getAnchor().getX()));
        else
            setX((int) (x + deltaX - getParent().getWindowX() - getParent().getWidth() * getAnchor().getX()));
    }

    default void setWindowY(int y) {
        int deltaY = getOrigin().isFromBottom() ? getHeight() : 0;
        if (getParent() == null)
            setY((int) (y + deltaY - GuiUtils.getHeight() * getAnchor().getY()));
        else
            setY((int) (y + deltaY - getParent().getWindowY() - getParent().getHeight() * getAnchor().getY()));
    }

    Anchor getAnchor();

    void setAnchor(Anchor anchor);

    Origin getOrigin();

    void setOrigin(Origin origin);

    Component getParent();

    void setParent(Component component);

    ComponentContext<?> getContext();

    void setContext(ComponentContext<?> context);

    boolean isFocused();

    void setFocused(boolean focused);

    boolean isVisible();

    void setVisible(boolean visible);

    void onRender();

    void onClick(int x, int y, ClickType type);

    void onHover(int x, int y);

    void onHold(int fromX, int fromY, int toX, int toY, ClickType type, MouseStatus status);

    void onScroll(int deltaX, int deltaY, ClickType type);

    boolean onKeyPressed(int keyCode, char key);

    boolean onKeyReleased(int keyCode);

    void onShow();

    void onClose();

    void addHandler(ComponentHandler handler);

    void removeHandler(ComponentHandler handler);

    List<ComponentHandler> getHandlers();
}
