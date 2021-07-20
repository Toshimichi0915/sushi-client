package net.toshimichi.sushi.events.render;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.toshimichi.sushi.events.CancellableEvent;
import net.toshimichi.sushi.events.EventTiming;

public class GameOverlayRenderEvent extends CancellableEvent {
    private final float partialTicks;
    private final ScaledResolution resolution;
    private final RenderGameOverlayEvent.ElementType type;

    public GameOverlayRenderEvent(EventTiming timing, float partialTicks, ScaledResolution resolution, RenderGameOverlayEvent.ElementType type) {
        super(timing);
        this.partialTicks = partialTicks;
        this.resolution = resolution;
        this.type = type;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    public ScaledResolution getResolution() {
        return resolution;
    }

    public RenderGameOverlayEvent.ElementType getType() {
        return type;
    }
}
