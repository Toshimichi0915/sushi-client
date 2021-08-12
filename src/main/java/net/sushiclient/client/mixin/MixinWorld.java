package net.sushiclient.client.mixin;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.client.LightUpdateEvent;
import net.sushiclient.client.events.world.RainStrengthGetEvent;
import net.sushiclient.client.events.world.ThunderStrengthGetEvent;
import net.sushiclient.client.events.world.WorldTimeGetEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public class MixinWorld {

    @Shadow(aliases = "provider")
    private WorldProvider provider;

    @Inject(at = @At("HEAD"), method = "getWorldTime", cancellable = true)
    public void getWorldTime(CallbackInfoReturnable<Long> cir) {
        WorldTimeGetEvent event = new WorldTimeGetEvent(EventTiming.PRE, provider.getWorldTime());
        EventHandlers.callEvent(event);
        cir.setReturnValue(event.getWorldTime());
        cir.cancel();
    }

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

    @Inject(method = "getThunderStrength", at = @At("RETURN"), cancellable = true)
    private void getThunderStrengthHead(float delta, CallbackInfoReturnable<Float> cir) {
        ThunderStrengthGetEvent event = new ThunderStrengthGetEvent(EventTiming.PRE, delta, cir.getReturnValueF());
        EventHandlers.callEvent(event);
        cir.setReturnValue(event.getValue());
    }

    @Inject(method = "getRainStrength", at = @At("RETURN"), cancellable = true)
    private void getRainStrengthHead(float delta, CallbackInfoReturnable<Float> cir) {
        RainStrengthGetEvent event = new RainStrengthGetEvent(EventTiming.PRE, delta, cir.getReturnValueF());
        EventHandlers.callEvent(event);
        cir.setReturnValue(event.getValue());
    }
}
