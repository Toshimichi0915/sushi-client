package net.toshimichi.sushi.mixin;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.player.PlayerPushEvent;
import net.toshimichi.sushi.utils.XrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class MixinEntity {

    @Inject(at = @At("HEAD"), method = "applyEntityCollision", cancellable = true)
    public void onPrePush(Entity entityIn, CallbackInfo ci) {
        if (!((Object) this instanceof EntityPlayerSP)) return;
        PlayerPushEvent event = new PlayerPushEvent(EventTiming.PRE, entityIn);
        EventHandlers.callEvent(event);
        if (event.isCancelled())
            ci.cancel();
    }


    @Inject(at = @At("TAIL"), method = "applyEntityCollision")
    public void onPostPush(Entity entityIn, CallbackInfo ci) {
        if (!((Object) this instanceof EntityPlayerSP)) return;
        PlayerPushEvent event = new PlayerPushEvent(EventTiming.POST, entityIn);
        EventHandlers.callEvent(event);
    }

    @Inject(at = @At("HEAD"), method = "getBrightnessForRender", cancellable = true)
    public void getBrightnessForRender(CallbackInfoReturnable<Integer> cir) {
        if (XrayUtils.isEnabled()) cir.cancel();
    }
}
