package net.sushiclient.client.handlers.forge;

import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.world.WorldRenderEvent;

public class WorldRenderHandler {

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent e) {
        WorldRenderEvent event = new WorldRenderEvent(EventTiming.POST, e.getContext(), e.getPartialTicks());
        EventHandlers.callEvent(event);
    }
}
