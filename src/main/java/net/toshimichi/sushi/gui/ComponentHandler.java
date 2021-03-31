package net.toshimichi.sushi.gui;

import net.toshimichi.sushi.events.input.ClickType;

public interface ComponentHandler {

    default void setX(int x) {
    }

    default void setY(int y) {
    }

    default void setWidth(int width) {
    }

    default void setHeight(int height) {
    }

    default void setMargin(Insets margin) {
    }

    default void setAnchor(Anchor anchor) {
    }

    default void setParent(Component parent) {
    }

    default void setOrigin(Origin origin) {
    }

    default void setContext(ComponentContext<?> context) {
    }

    default void setFocused(boolean focused) {
    }

    default void onRender() {
    }

    default void onClick(int x, int y, ClickType type) {
    }

    default void onHover(int x, int y) {
    }

    default void onHold(int fromX, int fromY, int toX, int toY, ClickType type, MouseStatus status) {
    }

    default void onScroll(int deltaX, int deltaY, ClickType type) {
    }

    default void onKeyPressed(int keyCode, char key) {
    }

    default void onKeyReleased(int keyCode) {
    }

    default void onShow() {
    }

    default void onClose() {
    }

}
