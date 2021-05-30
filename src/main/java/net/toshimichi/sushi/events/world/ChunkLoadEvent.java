package net.toshimichi.sushi.events.world;

import net.minecraft.world.chunk.Chunk;
import net.toshimichi.sushi.events.EventTiming;

public class ChunkLoadEvent extends ChunkEvent {
    public ChunkLoadEvent(EventTiming timing, Chunk chunk) {
        super(timing, chunk);
    }
}
