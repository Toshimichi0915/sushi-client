package net.toshimichi.sushi.events.player;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.toshimichi.sushi.events.Cancellable;
import net.toshimichi.sushi.events.Event;
import net.toshimichi.sushi.events.EventTiming;

public class BlockLeftClickEvent implements Event, Cancellable {

    private final EventTiming timing;
    private final PlayerInteractEvent.LeftClickBlock delegate;

    public BlockLeftClickEvent(EventTiming timing, PlayerInteractEvent.LeftClickBlock delegate) {
        this.timing = timing;
        this.delegate = delegate;
    }

    public EnumHand getHand() {
        return delegate.getHand();
    }

    public ItemStack getItemStack() {
        return delegate.getItemStack();
    }

    public BlockPos getPos() {
        return delegate.getPos();
    }

    public EnumFacing getFace() {
        return delegate.getFace();
    }

    public World getWorld() {
        return delegate.getWorld();
    }

    public Side getSide() {
        return delegate.getSide();
    }

    public EnumActionResult getCancellationResult() {
        return delegate.getCancellationResult();
    }

    public void setCancellationResult(EnumActionResult result) {
        delegate.setCancellationResult(result);
    }


    public Vec3d getHitVec() {
        return delegate.getHitVec();
    }

    public net.minecraftforge.fml.common.eventhandler.Event.Result getUseBlock() {
        return delegate.getUseBlock();
    }

    public net.minecraftforge.fml.common.eventhandler.Event.Result getUseItem() {
        return delegate.getUseItem();
    }

    public void setUseBlock(net.minecraftforge.fml.common.eventhandler.Event.Result triggerBlock) {
        delegate.setUseBlock(triggerBlock);
    }

    public void setUseItem(net.minecraftforge.fml.common.eventhandler.Event.Result triggerItem) {
        delegate.setUseItem(triggerItem);
    }

    @Override
    public boolean isCancelled() {
        return delegate.isCanceled();
    }

    @Override
    public void setCancelled(boolean cancelled) {
        delegate.setCanceled(cancelled);
    }

    @Override
    public EventTiming getTiming() {
        return timing;
    }

    @Override
    public boolean isAsync() {
        return false;
    }
}
