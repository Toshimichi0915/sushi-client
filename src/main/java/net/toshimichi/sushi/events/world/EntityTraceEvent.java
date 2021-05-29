package net.toshimichi.sushi.events.world;

import net.minecraft.entity.Entity;
import net.toshimichi.sushi.events.BaseEvent;
import net.toshimichi.sushi.events.EventTiming;

import java.util.ArrayList;
import java.util.List;

public class EntityTraceEvent extends BaseEvent {

    private final List<Entity> entities;

    public EntityTraceEvent(List<Entity> entities) {
        super(EventTiming.PRE);
        this.entities = new ArrayList<>(entities);
    }

    public List<Entity> getEntities() {
        return entities;
    }
}
