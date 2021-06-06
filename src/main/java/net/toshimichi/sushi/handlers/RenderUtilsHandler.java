package net.toshimichi.sushi.handlers;

import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.world.WorldRenderEvent;
import net.toshimichi.sushi.utils.render.RenderUtils;

public class RenderUtilsHandler {

    @EventHandler(timing = EventTiming.POST, priority = -100000)
    public void onWorldRender(WorldRenderEvent event) {
        RenderUtils.tick();
    }
}
