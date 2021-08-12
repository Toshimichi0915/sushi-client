package net.sushiclient.client.events.tick;

import net.sushiclient.client.events.BaseEvent;
import net.sushiclient.client.events.EventTiming;

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
