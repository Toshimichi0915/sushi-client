package net.toshimichi.sushi.handlers.forge;

import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.world.BlockHighlightEvent;

public class DrawBlockHighlightHandler {
    @SubscribeEvent
    public void onDrawBlockHighlight(DrawBlockHighlightEvent e) {
        BlockHighlightEvent event = new BlockHighlightEvent(EventTiming.PRE, e.getContext(), e.getPlayer(), e.getTarget(), e.getSubID(), e.getPartialTicks());
        EventHandlers.callEvent(event);
        if (event.isCancelled()) e.setCanceled(true);
    }
}
