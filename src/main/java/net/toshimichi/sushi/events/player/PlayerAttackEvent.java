package net.toshimichi.sushi.events.player;

import net.minecraft.entity.Entity;
import net.toshimichi.sushi.events.CancellableEvent;
import net.toshimichi.sushi.events.EventTiming;

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
