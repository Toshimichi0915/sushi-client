package net.toshimichi.sushi.mixin;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.MoverType;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.player.PlayerMotionUpdateEvent;
import net.toshimichi.sushi.events.player.PlayerMoveEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayerSP.class)
public class MixinEntityPlayerSP {

    @Inject(at = @At("HEAD"), method = "move", cancellable = true)
    public void onPreMove(MoverType type, double x, double y, double z, CallbackInfo ci) {
        PlayerMoveEvent event = new PlayerMoveEvent(type, x, y, z);
        EventHandlers.callEvent(event);
        if(event.isCancelled())
            ci.cancel();
    }

    @Inject(at = @At("TAIL"), method = "move")
    public void onPostMove(MoverType type, double x, double y, double z, CallbackInfo ci) {
        PlayerMoveEvent event = new PlayerMoveEvent(type, x, y, z);
        EventHandlers.callEvent(event);
    }

    @Inject(at = @At("HEAD"), method  = "onUpdateWalkingPlayer", cancellable = true)
    public void onPreUpdateWalkingPlayer(CallbackInfo ci) {
        PlayerMotionUpdateEvent event = new PlayerMotionUpdateEvent(EventTiming.PRE);
        EventHandlers.callEvent(event);
        if(event.isCancelled())
            ci.cancel();
    }
    @Inject(at = @At("TAIL"), method  = "onUpdateWalkingPlayer")
    public void onPostUpdateWalkingPlayer(CallbackInfo ci) {
        PlayerMotionUpdateEvent event = new PlayerMotionUpdateEvent(EventTiming.POST);
        EventHandlers.callEvent(event);
    }
}
