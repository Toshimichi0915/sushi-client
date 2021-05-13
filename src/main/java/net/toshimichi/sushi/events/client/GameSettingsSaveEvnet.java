package net.toshimichi.sushi.events.client;

import net.toshimichi.sushi.events.CancellableEvent;
import net.toshimichi.sushi.events.EventTiming;

public class GameSettingsSaveEvnet extends CancellableEvent {
    public GameSettingsSaveEvnet(EventTiming timing) {
        super(timing);
    }
}
