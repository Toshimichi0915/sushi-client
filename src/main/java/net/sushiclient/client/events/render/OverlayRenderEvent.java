package net.sushiclient.client.events.render;

import net.sushiclient.client.events.BaseEvent;
import net.sushiclient.client.events.EventTiming;

public class OverlayRenderEvent extends BaseEvent {
    public OverlayRenderEvent(EventTiming timing) {
        super(timing);
    }
}
