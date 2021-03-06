package net.toshimichi.sushi.gui;

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
            return getX();
        return (int) (getOrigin().getX() * getAnchor().getX() + getX());
    }

    default int getWindowY() {
        if (getOrigin() == null)
            return getY();
        return (int) (getOrigin().getY() * getAnchor().getY() + getY());
    }

    default void setWindowX(int x) {
        if (getOrigin() == null)
            setX(x);
        setX((int) (x - getOrigin().getX() * getAnchor().getX()));
    }

    default void setWindowY(int y) {
        if (getOrigin() == null)
            setY(y);
        setY((int) (y - getOrigin().getY() * getAnchor().getY()));
    }

    Anchor getAnchor();

    Component getOrigin();

    void onRender();

    void onClick(int x, int y, ClickType type);

    void onHold(int x, int y, ClickType type);

    void onKeyPressed(int keyCode);

    void onKeyReleased(int keyCode);
}
