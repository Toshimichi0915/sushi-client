package net.toshimichi.sushi.events.tick;

import net.toshimichi.sushi.events.BaseEvent;
import net.toshimichi.sushi.events.EventTiming;

public class GameTickEvent extends BaseEvent {
    public GameTickEvent(EventTiming timing) {
        super(timing);
    }
}
