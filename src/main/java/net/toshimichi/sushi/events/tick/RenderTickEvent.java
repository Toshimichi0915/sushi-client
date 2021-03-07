package net.toshimichi.sushi.events.tick;

import net.toshimichi.sushi.events.BaseEvent;
import net.toshimichi.sushi.events.EventTiming;

public class RenderTickEvent extends BaseEvent {

    public RenderTickEvent(EventTiming timing) {
        super(timing);
    }
}
