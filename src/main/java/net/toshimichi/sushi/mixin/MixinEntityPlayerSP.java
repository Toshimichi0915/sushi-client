package net.toshimichi.sushi.mixin;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.MoverType;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.player.PlayerMotionEvent;
import net.toshimichi.sushi.events.player.PlayerUpdateEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP {

    private boolean recalling;

    @Shadow
    public abstract void move(MoverType type, double x, double y, double z);

    @Inject(at = @At("HEAD"), method = "move", cancellable = true)
    public void onPreMove(MoverType type, double x, double y, double z, CallbackInfo ci) {
        if (recalling) {
            recalling = false;
            return;
        }
        PlayerMotionEvent event = new PlayerMotionEvent(EventTiming.PRE, type, x, y, z);
        EventHandlers.callEvent(event);
        if (!event.isCancelled()) {
            recalling = true;
            move(event.getType(), event.getX(), event.getY(), event.getZ());
        }
        ci.cancel();
    }

    @Inject(at = @At("TAIL"), method = "move")
    public void onPostMove(MoverType type, double x, double y, double z, CallbackInfo ci) {
        PlayerMotionEvent event = new PlayerMotionEvent(EventTiming.POST, type, x, y, z);
        EventHandlers.callEvent(event);
    }

    @Inject(at = @At("HEAD"), method = "onUpdateWalkingPlayer", cancellable = true)
    public void onPreUpdateWalkingPlayer(CallbackInfo ci) {
        PlayerUpdateEvent event = new PlayerUpdateEvent(EventTiming.PRE);
        EventHandlers.callEvent(event);
        if (event.isCancelled())
            ci.cancel();
    }

    @Inject(at = @At("TAIL"), method = "onUpdateWalkingPlayer")
    public void onPostUpdateWalkingPlayer(CallbackInfo ci) {
        PlayerUpdateEvent event = new PlayerUpdateEvent(EventTiming.POST);
        EventHandlers.callEvent(event);
    }
}
