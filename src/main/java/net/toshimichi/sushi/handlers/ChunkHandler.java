package net.toshimichi.sushi.handlers;

import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.world.ChunkLoadEvent;
import net.toshimichi.sushi.events.world.ChunkUnloadEvent;

public class ChunkHandler {

    @SubscribeEvent
    public void onChunkLoad(ChunkEvent.Load e) {
        EventHandlers.callEvent(new ChunkLoadEvent(EventTiming.POST, e.getChunk()));
    }

    @SubscribeEvent
    public void onChunkUnload(ChunkEvent.Unload e) {
        EventHandlers.callEvent(new ChunkUnloadEvent(EventTiming.PRE, e.getChunk()));
    }
}
