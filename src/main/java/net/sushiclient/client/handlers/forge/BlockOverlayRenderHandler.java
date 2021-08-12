package net.sushiclient.client.handlers.forge;

import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.render.BlockOverlayRenderEvent;

public class BlockOverlayRenderHandler {
    @SubscribeEvent
    public void onBlockOverlayRender(RenderBlockOverlayEvent e) {
        BlockOverlayRenderEvent event = new BlockOverlayRenderEvent(e.getPlayer(), e.getRenderPartialTicks(), e.getOverlayType(), e.getBlockForOverlay(), e.getBlockPos());
        EventHandlers.callEvent(event);
        e.setCanceled(event.isCancelled());
    }
}
