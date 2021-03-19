package net.toshimichi.sushi.events.client;

import net.toshimichi.sushi.events.CancellableEvent;
import net.toshimichi.sushi.events.EventTiming;

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
