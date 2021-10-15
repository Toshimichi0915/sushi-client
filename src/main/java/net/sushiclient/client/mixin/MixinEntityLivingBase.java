package net.sushiclient.client.mixin;

import net.minecraft.entity.EntityLivingBase;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.player.PlayerUpdateEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityLivingBase.class)
public class MixinEntityLivingBase {

    @Inject(at = @At("HEAD"), method = "onLivingUpdate", cancellable = true)
    public void preOnLivingUpdate(CallbackInfo ci) {
        if (!((Object) this instanceof EntityLivingBase)) return;
        PlayerUpdateEvent event = new PlayerUpdateEvent(EventTiming.PRE);
        EventHandlers.callEvent(event);
        if (event.isCancelled()) ci.cancel();
    }

    @Inject(at = @At("TAIL"), method = "onLivingUpdate")
    public void postOnLivingUpdate(CallbackInfo ci) {
        if (!((Object) this instanceof EntityLivingBase)) return;
        EventHandlers.callEvent(new PlayerUpdateEvent(EventTiming.POST));
    }
}
