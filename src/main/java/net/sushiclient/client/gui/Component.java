package net.sushiclient.client.gui;

import net.sushiclient.client.events.input.ClickType;
import net.sushiclient.client.utils.render.GuiUtils;

import java.util.List;

public interface Component {
    double getX();

    double getY();

    void setX(double x);

    void setY(double y);

    double getWidth();

    double getHeight();

    void setWidth(double width);

    void setHeight(double height);

    Insets getMargin();

    void setMargin(Insets margin);

    default double getWindowX() {
        double deltaX = getOrigin().isFromRight() ? getWidth() : 0;
        if (getParent() == null)
            return getX() - deltaX + GuiUtils.getWidth() * getAnchor().getX();
        return getX() - deltaX + getParent().getWindowX() + getParent().getWidth() * getAnchor().getX();
    }

    default double getWindowY() {
        double deltaY = getOrigin().isFromBottom() ? getHeight() : 0;
        if (getParent() == null)
            return getY() - deltaY + GuiUtils.getHeight() * getAnchor().getY();
        return getY() - deltaY + getParent().getWindowY() + getParent().getHeight() * getAnchor().getY();
    }

    default void setWindowX(double x) {
        double deltaX = getOrigin().isFromRight() ? getWidth() : 0;
        if (getParent() == null)
            setX(x + deltaX - GuiUtils.getWidth() * getAnchor().getX());
        else
            setX(x + deltaX - getParent().getWindowX() - getParent().getWidth() * getAnchor().getX());
    }

    default void setWindowY(double y) {
        double deltaY = getOrigin().isFromBottom() ? getHeight() : 0;
        if (getParent() == null)
            setY(y + deltaY - GuiUtils.getHeight() * getAnchor().getY());
        else
            setY(y + deltaY - getParent().getWindowY() - getParent().getHeight() * getAnchor().getY());
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

    void onRelocate();

    void onClick(int x, int y, ClickType type);

    void onHover(int x, int y);

    void onHold(int fromX, int fromY, int toX, int toY, ClickType type, MouseStatus status);

    void onScroll(int deltaX, int deltaY);

    boolean onKeyPressed(int keyCode, char key);

    boolean onKeyReleased(int keyCode);

    void onShow();

    void onClose();

    void addHandler(ComponentHandler handler);

    boolean removeHandler(ComponentHandler handler);

    List<ComponentHandler> getHandlers();
}
