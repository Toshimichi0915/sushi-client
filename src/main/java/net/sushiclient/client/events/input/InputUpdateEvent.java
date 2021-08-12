package net.sushiclient.client.events.input;

import net.sushiclient.client.events.CancellableEvent;
import net.sushiclient.client.events.EventTiming;

public class InputUpdateEvent extends CancellableEvent {

    public InputUpdateEvent(EventTiming timing) {
        super(timing);
    }
}
