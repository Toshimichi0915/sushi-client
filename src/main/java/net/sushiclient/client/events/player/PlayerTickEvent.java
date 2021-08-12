package net.sushiclient.client.events.player;

import net.sushiclient.client.events.CancellableEvent;
import net.sushiclient.client.events.EventTiming;

public class PlayerTickEvent extends CancellableEvent {

    public PlayerTickEvent(EventTiming timing) {
        super(timing);
    }
}
