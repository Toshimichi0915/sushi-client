package net.sushiclient.client.events.world;

import net.minecraft.world.chunk.Chunk;
import net.sushiclient.client.events.EventTiming;

public class ChunkUnloadEvent extends ChunkEvent {
    public ChunkUnloadEvent(EventTiming timing, Chunk chunk) {
        super(timing, chunk);
    }
}
