package net.sushiclient.client.mixin;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.sushiclient.client.utils.world.BlockVisibility;
import net.sushiclient.client.utils.world.XrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileEntityRendererDispatcher.class)
public class MixinTileEntityRendererDispatcher {

    @Inject(at = @At("HEAD"), method = "render(Lnet/minecraft/tileentity/TileEntity;FI)V", cancellable = true)
    public void render(TileEntity tileentityIn, float partialTicks, int destroyStage, CallbackInfo ci) {
        BlockVisibility visibility = XrayUtils.getBlockVisibility(tileentityIn.getBlockType().getDefaultState().getBlock());
        if (visibility == BlockVisibility.INVISIBLE) ci.cancel();
    }
}
