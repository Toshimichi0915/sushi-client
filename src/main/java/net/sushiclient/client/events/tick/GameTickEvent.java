package net.sushiclient.client.events.tick;

import net.sushiclient.client.events.BaseEvent;
import net.sushiclient.client.events.EventTiming;

public class GameTickEvent extends BaseEvent {
    public GameTickEvent(EventTiming timing) {
        super(timing);
    }
}
