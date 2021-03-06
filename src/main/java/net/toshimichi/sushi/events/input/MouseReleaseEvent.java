package net.toshimichi.sushi.events.input;

import net.toshimichi.sushi.events.CancellableEvent;

public class MouseReleaseEvent extends CancellableEvent {
    private final ClickType clickType;

    public MouseReleaseEvent(ClickType clickType) {
        this.clickType = clickType;
    }

    public ClickType getClickType() {
        return clickType;
    }
}
