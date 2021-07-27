package net.toshimichi.sushi.events.player;

import net.toshimichi.sushi.events.CancellableEvent;
import net.toshimichi.sushi.events.EventTiming;

public class PlayerTickEvent extends CancellableEvent {

    public PlayerTickEvent(EventTiming timing) {
        super(timing);
    }
}
