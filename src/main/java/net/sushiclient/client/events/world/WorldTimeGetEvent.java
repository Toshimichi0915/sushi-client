package net.sushiclient.client.events.world;

import net.sushiclient.client.events.BaseEvent;
import net.sushiclient.client.events.EventTiming;

public class WorldTimeGetEvent extends BaseEvent {
    private long worldTime;

    public WorldTimeGetEvent(EventTiming timing, long worldTime) {
        super(timing);
        this.worldTime = worldTime;
    }

    public long getWorldTime() {
        return worldTime;
    }

    public void setWorldTime(long worldTime) {
        this.worldTime = worldTime;
    }
}
