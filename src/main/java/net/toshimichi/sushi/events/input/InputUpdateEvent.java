package net.toshimichi.sushi.events.input;

import net.toshimichi.sushi.events.CancellableEvent;
import net.toshimichi.sushi.events.EventTiming;

public class InputUpdateEvent extends CancellableEvent {

    public InputUpdateEvent(EventTiming timing) {
        super(timing);
    }
}
