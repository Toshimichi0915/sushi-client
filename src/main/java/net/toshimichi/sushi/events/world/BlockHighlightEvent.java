package net.toshimichi.sushi.events.world;

import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.toshimichi.sushi.events.CancellableEvent;
import net.toshimichi.sushi.events.EventTiming;

public class BlockHighlightEvent extends CancellableEvent implements WorldEvent {

    private final RenderGlobal context;
    private final EntityPlayer player;
    private final RayTraceResult target;
    private final int subID;
    private final float partialTicks;

    public BlockHighlightEvent(EventTiming timing, RenderGlobal context, EntityPlayer player, RayTraceResult target, int subID, float partialTicks) {
        super(timing);
        this.context = context;
        this.player = player;
        this.target = target;
        this.subID = subID;
        this.partialTicks = partialTicks;
    }

    public RenderGlobal getContext() {
        return context;
    }

    public EntityPlayer getPlayer() {
        return player;
    }

    public RayTraceResult getTarget() {
        return target;
    }

    public int getSubID() {
        return subID;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    @Override
    public World getWorld() {
        return player.world;
    }
}
