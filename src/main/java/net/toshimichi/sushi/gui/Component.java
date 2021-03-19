package net.toshimichi.sushi.gui;

import net.minecraft.client.Minecraft;
import net.toshimichi.sushi.events.input.ClickType;

public interface Component {
    int getX();

    int getY();

    void setX(int x);

    void setY(int y);

    int getWidth();

    int getHeight();

    default int getWindowX() {
        if (getOrigin() == null)
            return (int) (Minecraft.getMinecraft().displayWidth * getAnchor().getX() + getX());
        return (int) (getX() + getOrigin().getWindowX() + getOrigin().getWidth() * getAnchor().getX());
    }

    default int getWindowY() {
        if (getOrigin() == null)
            return (int) (Minecraft.getMinecraft().displayHeight * getAnchor().getY() + getY());
        return (int) (getY() + getOrigin().getWindowY() + getOrigin().getHeight() * getAnchor().getY());
    }

    default void setWindowX(int x) {
        if (getOrigin() == null)
            setX(x);
        setX((int) (x - getOrigin().getWindowX() - getOrigin().getWidth() * getAnchor().getX()));
    }

    default void setWindowY(int y) {
        if (getOrigin() == null)
            setY(y);
        setY((int) (y - getOrigin().getWindowY() - getOrigin().getHeight() * getAnchor().getY()));
    }

    Anchor getAnchor();

    void setAnchor(Anchor anchor);

    Component getOrigin();

    void setOrigin(Component component);

    boolean isFocused();

    void setFocused(boolean focused);

    boolean isVisible();

    void setVisible(boolean visible);

    void onRender();

    void onClick(int x, int y, ClickType type);

    void onHold(int fromX, int fromY, int toX, int toY, ClickType type, MouseStatus status);

    void onScroll(int deltaX, int deltaY, ClickType type);

    void onKeyPressed(int keyCode);

    void onKeyReleased(int keyCode);
}
