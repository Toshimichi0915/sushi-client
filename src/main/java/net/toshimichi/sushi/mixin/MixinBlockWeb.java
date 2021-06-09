package net.toshimichi.sushi.mixin;

import net.minecraft.block.BlockWeb;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.player.BlockCollisionEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockWeb.class)
public class MixinBlockWeb {
    @Inject(at = @At("HEAD"), method = "onEntityCollision", cancellable = true)
    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn, CallbackInfo ci) {
        BlockCollisionEvent event = new BlockCollisionEvent(worldIn, pos, state, entityIn, ((BlockWeb) (Object) this));
        EventHandlers.callEvent(event);
        if (event.isCancelled()) ci.cancel();
    }
}
