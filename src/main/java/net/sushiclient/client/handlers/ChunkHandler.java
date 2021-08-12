package net.sushiclient.client.handlers;

import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.world.ChunkLoadEvent;
import net.sushiclient.client.events.world.ChunkUnloadEvent;

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
