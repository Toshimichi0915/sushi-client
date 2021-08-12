package net.sushiclient.client.events.render;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.sushiclient.client.events.CancellableEvent;

public class BlockOverlayRenderEvent extends CancellableEvent {

    private final EntityPlayer player;
    private final float renderPartialTicks;
    private final RenderBlockOverlayEvent.OverlayType overlayType;
    private final IBlockState blockForOverlay;
    private final BlockPos blockPos;

    public BlockOverlayRenderEvent(EntityPlayer player, float renderPartialTicks, RenderBlockOverlayEvent.OverlayType overlayType, IBlockState blockForOverlay, BlockPos blockPos) {
        this.player = player;
        this.renderPartialTicks = renderPartialTicks;
        this.overlayType = overlayType;
        this.blockForOverlay = blockForOverlay;
        this.blockPos = blockPos;
    }

    public EntityPlayer getPlayer() {
        return player;
    }

    public float getRenderPartialTicks() {
        return renderPartialTicks;
    }

    public RenderBlockOverlayEvent.OverlayType getOverlayType() {
        return overlayType;
    }

    public IBlockState getBlockForOverlay() {
        return blockForOverlay;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }
}
