package net.toshimichi.sushi.events.tick;

import net.toshimichi.sushi.events.BaseEvent;
import net.toshimichi.sushi.events.EventTiming;

public class OverlayRenderEvent extends BaseEvent {
    public OverlayRenderEvent(EventTiming timing) {
        super(timing);
    }
}
