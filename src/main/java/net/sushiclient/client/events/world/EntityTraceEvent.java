package net.sushiclient.client.events.world;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.sushiclient.client.events.BaseEvent;
import net.sushiclient.client.events.EventTiming;

import java.util.ArrayList;
import java.util.List;

public class EntityTraceEvent extends BaseEvent implements WorldEvent {

    private final World world;
    private final List<Entity> entities;

    public EntityTraceEvent(World world, List<Entity> entities) {
        super(EventTiming.PRE);
        this.world = world;
        this.entities = new ArrayList<>(entities);
    }

    public List<Entity> getEntities() {
        return entities;
    }

    @Override
    public World getWorld() {
        return world;
    }
}
