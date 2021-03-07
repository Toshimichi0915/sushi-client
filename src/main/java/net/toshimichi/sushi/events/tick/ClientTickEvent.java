package net.toshimichi.sushi.events.tick;

import net.toshimichi.sushi.events.BaseEvent;
import net.toshimichi.sushi.events.EventTiming;

public class ClientTickEvent extends BaseEvent {

    public ClientTickEvent(EventTiming timing) {
        super(timing);
    }
}
