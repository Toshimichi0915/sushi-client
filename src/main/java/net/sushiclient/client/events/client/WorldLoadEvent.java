package net.sushiclient.client.events.client;

import net.minecraft.client.multiplayer.WorldClient;
import net.sushiclient.client.events.BaseEvent;
import net.sushiclient.client.events.EventTiming;

public class WorldLoadEvent extends BaseEvent {

    private final WorldClient client;

    public WorldLoadEvent(EventTiming timing, WorldClient client) {
        super(timing);
        this.client = client;
    }

    public WorldClient getClient() {
        return client;
    }
}
