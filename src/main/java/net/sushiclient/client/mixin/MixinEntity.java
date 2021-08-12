package net.sushiclient.client.mixin;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.player.PlayerPushEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
}
