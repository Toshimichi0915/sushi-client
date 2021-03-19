package net.toshimichi.sushi.events.input;

import net.toshimichi.sushi.events.CancellableEvent;

public class MouseEvent extends CancellableEvent {

    private final ClickType clickType;

    public MouseEvent(ClickType clickType) {
        this.clickType = clickType;
    }

    public ClickType getClickType() {
        return clickType;
    }
}
