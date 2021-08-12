package net.sushiclient.client.mixin;

import net.minecraft.util.MovementInputFromOptions;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.input.InputUpdateEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MovementInputFromOptions.class)
public class MixinMovementInputFromOptions {

    @Inject(at = @At("HEAD"), method = "updatePlayerMoveState", cancellable = true)
    public void onPreUpdatePlayerMoveState(CallbackInfo ci) {
        InputUpdateEvent event = new InputUpdateEvent(EventTiming.PRE);
        EventHandlers.callEvent(event);
        if (event.isCancelled()) ci.cancel();
    }

    @Inject(at = @At("TAIL"), method = "updatePlayerMoveState")
    public void onPostUpdatePlayerMoveState(CallbackInfo ci) {
        EventHandlers.callEvent(new InputUpdateEvent(EventTiming.POST));
    }
}
