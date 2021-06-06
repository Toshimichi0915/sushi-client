package net.toshimichi.sushi.events.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.toshimichi.sushi.events.Cancellable;
import net.toshimichi.sushi.events.Event;
import net.toshimichi.sushi.events.EventTiming;

public class BlockLeftClickEvent extends PlayerInteractEvent.LeftClickBlock implements Event, Cancellable {

    private final EventTiming timing;

    public BlockLeftClickEvent(EventTiming timing, EntityPlayer player, BlockPos pos, EnumFacing face, Vec3d hitVec) {
        super(player, pos, face, hitVec);
        this.timing = timing;
    }

    @Override
    public boolean isCancelled() {
        return super.isCanceled();
    }

    @Override
    public void setCancelled(boolean cancelled) {
        super.setCanceled(cancelled);
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
