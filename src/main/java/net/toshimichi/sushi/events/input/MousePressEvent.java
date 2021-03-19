package net.toshimichi.sushi.events.input;

public class MousePressEvent extends MouseEvent {

    public MousePressEvent(ClickType clickType) {
        super(clickType);
    }
}
