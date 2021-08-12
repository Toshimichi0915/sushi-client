package net.sushiclient.client.events.player;

import net.sushiclient.client.events.CancellableEvent;
import net.sushiclient.client.events.EventTiming;

public class PlayerUpdateEvent extends CancellableEvent {
    public PlayerUpdateEvent(EventTiming timing) {
        super(timing);
    }
}
