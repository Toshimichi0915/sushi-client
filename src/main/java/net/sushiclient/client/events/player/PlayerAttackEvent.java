package net.sushiclient.client.events.player;

import net.minecraft.entity.Entity;
import net.sushiclient.client.events.CancellableEvent;
import net.sushiclient.client.events.EventTiming;

public class PlayerAttackEvent extends CancellableEvent {

    private final Entity target;

    public PlayerAttackEvent(EventTiming timing, Entity target) {
        super(timing);
        this.target = target;
    }

    public Entity getTarget() {
        return target;
    }

}
