package net.sushiclient.client.handlers;

import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.world.WorldRenderEvent;
import net.sushiclient.client.utils.render.RenderUtils;

public class RenderUtilsHandler {

    @EventHandler(timing = EventTiming.POST, priority = -100000)
    public void onWorldRender(WorldRenderEvent event) {
        RenderUtils.tick();
    }
}
