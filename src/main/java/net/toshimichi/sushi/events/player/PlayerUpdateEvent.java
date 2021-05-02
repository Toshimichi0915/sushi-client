package net.toshimichi.sushi.events.player;

import net.toshimichi.sushi.events.CancellableEvent;
import net.toshimichi.sushi.events.EventTiming;

public class PlayerUpdateEvent extends CancellableEvent {

    public PlayerUpdateEvent(EventTiming timing) {
        super(timing);
    }
}
