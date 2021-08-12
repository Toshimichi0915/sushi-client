package net.sushiclient.client.events.tick;

import net.sushiclient.client.events.BaseEvent;
import net.sushiclient.client.events.EventTiming;

public class ClientTickEvent extends BaseEvent {

    public ClientTickEvent(EventTiming timing) {
        super(timing);
    }
}
