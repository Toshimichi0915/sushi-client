package net.toshimichi.sushi.mixin;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockFluidRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.toshimichi.sushi.utils.BlockVisibility;
import net.toshimichi.sushi.utils.XrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockFluidRenderer.class)
public class MixinBlockFluidRenderer {

    @Inject(at = @At("HEAD"), method = "renderFluid", cancellable = true)
    public void renderFluid(IBlockAccess blockAccess, IBlockState blockStateIn, BlockPos blockPosIn, BufferBuilder bufferBuilderIn, CallbackInfoReturnable<Boolean> cir) {
        BlockVisibility visibility = XrayUtils.getBlockVisibility(blockStateIn.getBlock());
        if (visibility == BlockVisibility.INVISIBLE) cir.cancel();
    }
}
