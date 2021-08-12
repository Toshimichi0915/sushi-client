package net.sushiclient.client.mixin;

import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.sushiclient.client.utils.world.BlockVisibility;
import net.sushiclient.client.utils.world.XrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockStateContainer.StateImplementation.class)
public class MixinStateImplementation {

    @Inject(at = @At("HEAD"), method = "shouldSideBeRendered", cancellable = true)
    public void shouldSideBeRendered(IBlockAccess blockAccess, BlockPos pos, EnumFacing facing, CallbackInfoReturnable<Boolean> cir) {
        BlockVisibility visibility = XrayUtils.getBlockVisibility(blockAccess.getBlockState(pos.offset(facing)).getBlock());
        if (visibility == BlockVisibility.INVISIBLE)
            cir.setReturnValue(true);
    }
}
