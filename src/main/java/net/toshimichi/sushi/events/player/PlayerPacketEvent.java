package net.toshimichi.sushi.events.player;

import net.toshimichi.sushi.events.CancellableEvent;
import net.toshimichi.sushi.events.EventTiming;

public class PlayerPacketEvent extends CancellableEvent {

    public PlayerPacketEvent(EventTiming timing) {
        super(timing);
    }
}
