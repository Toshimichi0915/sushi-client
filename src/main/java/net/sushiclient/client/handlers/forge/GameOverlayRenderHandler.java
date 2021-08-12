package net.sushiclient.client.handlers.forge;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.render.GameOverlayRenderEvent;

public class GameOverlayRenderHandler {

    @SubscribeEvent
    public void onPreGameOverlayRender(RenderGameOverlayEvent.Pre e) {
        GameOverlayRenderEvent event = new GameOverlayRenderEvent(EventTiming.PRE, e.getPartialTicks(), e.getResolution(), e.getType());
        EventHandlers.callEvent(event);
        e.setCanceled(event.isCancelled());
    }

    @SubscribeEvent
    public void onPostGameOverlayRender(RenderGameOverlayEvent.Post e) {
        GameOverlayRenderEvent event = new GameOverlayRenderEvent(EventTiming.POST, e.getPartialTicks(), e.getResolution(), e.getType());
        EventHandlers.callEvent(event);
    }
}
