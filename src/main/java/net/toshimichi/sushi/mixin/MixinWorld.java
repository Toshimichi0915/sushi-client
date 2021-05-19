package net.toshimichi.sushi.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.client.LightUpdateEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public class MixinWorld {
    @Inject(at = @At("HEAD"), method = "checkLightFor", cancellable = true)
    public void onPreCheckLightFor(EnumSkyBlock lightType, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        LightUpdateEvent event = new LightUpdateEvent(EventTiming.PRE, lightType, pos);
        EventHandlers.callEvent(event);
        if (event.isCancelled()) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Inject(at = @At("TAIL"), method = "checkLightFor")
    public void onPostCheckLightFor(EnumSkyBlock lightType, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        LightUpdateEvent event = new LightUpdateEvent(EventTiming.POST, lightType, pos);
        EventHandlers.callEvent(event);
    }
}
