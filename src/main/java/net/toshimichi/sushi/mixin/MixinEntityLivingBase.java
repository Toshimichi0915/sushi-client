package net.toshimichi.sushi.mixin;

import net.minecraft.entity.EntityLivingBase;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.player.PlayerUpdateEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityLivingBase.class)
public class MixinEntityLivingBase {

    @Inject(at = @At("HEAD"), method = "onLivingUpdate", cancellable = true)
    public void onPreLivingUpdate(CallbackInfo ci) {
        if (!((Object) this instanceof EntityLivingBase)) return;
        PlayerUpdateEvent event = new PlayerUpdateEvent(EventTiming.PRE);
        EventHandlers.callEvent(event);
        if (event.isCancelled()) ci.cancel();
    }

    @Inject(at = @At("TAIL"), method = "onLivingUpdate")
    public void onPostLivingUpdate(CallbackInfo ci) {
        if (!((Object) this instanceof EntityLivingBase)) return;
        EventHandlers.callEvent(new PlayerUpdateEvent(EventTiming.POST));
    }
}
