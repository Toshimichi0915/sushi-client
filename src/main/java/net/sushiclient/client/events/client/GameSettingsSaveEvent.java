package net.sushiclient.client.events.client;

import net.sushiclient.client.events.CancellableEvent;
import net.sushiclient.client.events.EventTiming;

public class GameSettingsSaveEvent extends CancellableEvent {
    public GameSettingsSaveEvent(EventTiming timing) {
        super(timing);
    }
}
