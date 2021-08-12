package net.sushiclient.client.events.player;

import net.minecraft.entity.Entity;
import net.sushiclient.client.events.CancellableEvent;
import net.sushiclient.client.events.EventTiming;

public class PlayerPushEvent extends CancellableEvent {

    private final Entity entity;

    public PlayerPushEvent(EventTiming timing, Entity entity) {
        super(timing);
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }
}
