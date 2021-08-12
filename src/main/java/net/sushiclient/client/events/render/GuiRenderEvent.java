package net.sushiclient.client.events.render;

import net.sushiclient.client.events.BaseEvent;
import net.sushiclient.client.events.EventTiming;

public class GuiRenderEvent extends BaseEvent {
    public GuiRenderEvent(EventTiming timing) {
        super(timing);
    }
}
