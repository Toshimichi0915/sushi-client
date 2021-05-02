package net.toshimichi.sushi.events.player;

import net.minecraft.entity.Entity;
import net.toshimichi.sushi.events.CancellableEvent;
import net.toshimichi.sushi.events.EventTiming;

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
