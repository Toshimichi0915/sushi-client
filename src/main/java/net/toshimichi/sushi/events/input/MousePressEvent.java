package net.toshimichi.sushi.events.input;

import net.toshimichi.sushi.events.CancellableEvent;

public class MousePressEvent extends CancellableEvent {
    private final ClickType clickType;

    public MousePressEvent(ClickType clickType) {
        this.clickType = clickType;
    }

    public ClickType getClickType() {
        return clickType;
    }
}
