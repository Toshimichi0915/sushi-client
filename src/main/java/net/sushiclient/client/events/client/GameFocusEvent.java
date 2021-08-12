package net.sushiclient.client.events.client;

import net.sushiclient.client.events.CancellableEvent;
import net.sushiclient.client.events.EventTiming;

public class GameFocusEvent extends CancellableEvent {

    private final boolean focused;

    public GameFocusEvent(EventTiming timing, boolean focused) {
        super(timing);
        this.focused = focused;
    }

    public boolean isFocused() {
        return focused;
    }

}
