package net.toshimichi.sushi.events.world;

import net.minecraft.client.renderer.RenderGlobal;
import net.toshimichi.sushi.events.BaseEvent;
import net.toshimichi.sushi.events.EventTiming;

public class WorldRenderEvent extends BaseEvent {
    private final RenderGlobal context;
    private final float partialTicks;

    public WorldRenderEvent(EventTiming timing, RenderGlobal context, float partialTicks) {
        super(timing);
        this.context = context;
        this.partialTicks = partialTicks;
    }

    public RenderGlobal getContext() {
        return context;
    }

    public float getPartialTicks() {
        return partialTicks;
    }
}
