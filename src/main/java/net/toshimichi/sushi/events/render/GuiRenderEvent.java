package net.toshimichi.sushi.events.render;

import net.toshimichi.sushi.events.BaseEvent;
import net.toshimichi.sushi.events.EventTiming;

public class GuiRenderEvent extends BaseEvent {
    public GuiRenderEvent(EventTiming timing) {
        super(timing);
    }
}
