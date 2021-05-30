package net.toshimichi.sushi.events.world;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.toshimichi.sushi.events.BaseEvent;
import net.toshimichi.sushi.events.EventTiming;

public class ChunkEvent extends BaseEvent implements WorldEvent {

    private final Chunk chunk;

    public ChunkEvent(EventTiming timing, Chunk chunk) {
        super(timing);
        this.chunk = chunk;
    }

    public Chunk getChunk() {
        return chunk;
    }

    @Override
    public World getWorld() {
        return chunk.getWorld();
    }
}
