package net.sushiclient.client.events.player;

import net.sushiclient.client.events.CancellableEvent;
import net.sushiclient.client.events.EventTiming;

public class PlayerPacketEvent extends CancellableEvent {

    public PlayerPacketEvent(EventTiming timing) {
        super(timing);
    }
}
