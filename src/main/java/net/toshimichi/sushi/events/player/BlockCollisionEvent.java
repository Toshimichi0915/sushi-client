package net.toshimichi.sushi.events.player;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.toshimichi.sushi.events.CancellableEvent;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class BlockCollisionEvent extends CancellableEvent {
    private final World worldIn;
    private final BlockPos pos;
    private final IBlockState state;
    private final Entity entityIn;
    private final Block block;

    public BlockCollisionEvent(World worldIn, BlockPos pos, IBlockState state, Entity entityIn, Block block) {
        this.worldIn = worldIn;
        this.pos = pos;
        this.state = state;
        this.entityIn = entityIn;
        this.block = block;
    }

    public World getWorldIn() {
        return worldIn;
    }

    public BlockPos getPos() {
        return pos;
    }

    public IBlockState getState() {
        return state;
    }

    public Entity getEntity() {
        return entityIn;
    }

    public Block getBlock() {
        return block;
    }
}
