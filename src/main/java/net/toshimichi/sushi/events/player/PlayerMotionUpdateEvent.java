package net.toshimichi.sushi.events.player;

import net.toshimichi.sushi.events.CancellableEvent;
import net.toshimichi.sushi.events.EventTiming;

public class PlayerMotionUpdateEvent extends CancellableEvent {

    public PlayerMotionUpdateEvent() {
    }

    public PlayerMotionUpdateEvent(EventTiming timing) {
        super(timing);
    }
}
