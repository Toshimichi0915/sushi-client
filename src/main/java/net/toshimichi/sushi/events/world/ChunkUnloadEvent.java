package net.toshimichi.sushi.events.world;

import net.minecraft.world.chunk.Chunk;
import net.toshimichi.sushi.events.EventTiming;

public class ChunkUnloadEvent extends ChunkEvent {
    public ChunkUnloadEvent(EventTiming timing, Chunk chunk) {
        super(timing, chunk);
    }
}
