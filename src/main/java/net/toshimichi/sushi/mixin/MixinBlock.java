package net.toshimichi.sushi.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.toshimichi.sushi.utils.BlockVisibility;
import net.toshimichi.sushi.utils.XrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class MixinBlock {

    @Inject(at = @At("HEAD"), method = "getLightOpacity(Lnet/minecraft/block/state/IBlockState;)I", cancellable = true)
    public void getLightValue(IBlockState state, CallbackInfoReturnable<Integer> cir) {
        if (XrayUtils.getBlockVisibility(state.getBlock()) == BlockVisibility.INVISIBLE) cir.setReturnValue(0);
    }
}
