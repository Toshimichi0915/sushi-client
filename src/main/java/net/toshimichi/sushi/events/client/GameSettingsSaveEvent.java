package net.toshimichi.sushi.events.client;

import net.toshimichi.sushi.events.CancellableEvent;
import net.toshimichi.sushi.events.EventTiming;

public class GameSettingsSaveEvent extends CancellableEvent {
    public GameSettingsSaveEvent(EventTiming timing) {
        super(timing);
    }
}
