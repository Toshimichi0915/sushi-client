package net.toshimichi.sushi.events.tick;

import net.toshimichi.sushi.events.BaseEvent;
import net.toshimichi.sushi.events.EventTiming;

public class RenderTickEvent extends BaseEvent {

    private final float partialTicks;

    public RenderTickEvent(EventTiming timing, float partialTicks) {
        super(timing);
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return partialTicks;
    }
}
