package net.toshimichi.sushi.events.world;

import net.toshimichi.sushi.events.BaseEvent;
import net.toshimichi.sushi.events.EventTiming;

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
