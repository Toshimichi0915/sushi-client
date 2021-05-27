package net.toshimichi.sushi.handlers.forge;

import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.world.WorldRenderEvent;

public class WorldRenderHandler {
    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent e) {
        WorldRenderEvent event = new WorldRenderEvent(EventTiming.POST, e.getContext(), e.getPartialTicks());
        EventHandlers.callEvent(event);
    }
}
