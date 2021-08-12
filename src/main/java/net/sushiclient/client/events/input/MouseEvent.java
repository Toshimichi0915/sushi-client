package net.sushiclient.client.events.input;

import net.sushiclient.client.events.CancellableEvent;

public class MouseEvent extends CancellableEvent {

    private final ClickType clickType;

    public MouseEvent(ClickType clickType) {
        this.clickType = clickType;
    }

    public ClickType getClickType() {
        return clickType;
    }
}
