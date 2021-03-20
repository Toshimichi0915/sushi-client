package net.toshimichi.sushi.events.client;

import net.minecraft.client.multiplayer.WorldClient;
import net.toshimichi.sushi.events.BaseEvent;
import net.toshimichi.sushi.events.EventTiming;

public class LoadWorldEvent extends BaseEvent {

    private final WorldClient client;

    public LoadWorldEvent(EventTiming timing, WorldClient client) {
        super(timing);
        this.client = client;
    }

    public WorldClient getClient() {
        return client;
    }
}
