package net.toshimichi.sushi.mixin;

import net.minecraft.client.Minecraft;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.client.GameFocusEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {

    private boolean callGameFocusEvent(EventTiming timing, boolean focused) {
        GameFocusEvent event = new GameFocusEvent(timing, focused);
        EventHandlers.callEvent(event);
        return event.isCancelled();
    }

    @Inject(at = @At("HEAD"), method = "setIngameFocus", cancellable = true)
    public void onFocusHead(CallbackInfo info) {
        if (callGameFocusEvent(EventTiming.PRE, true)) info.cancel();
    }

    @Inject(at = @At("HEAD"), method = "setIngameNotInFocus", cancellable = true)
    public void onNotInFocusHead(CallbackInfo info) {
        if (callGameFocusEvent(EventTiming.PRE, false)) info.cancel();
    }

    @Inject(at = @At("TAIL"), method = "setIngameFocus", cancellable = true)
    public void onFocusTail(CallbackInfo info) {
        if (callGameFocusEvent(EventTiming.POST, true)) info.cancel();
    }

    @Inject(at = @At("TAIL"), method = "setIngameNotInFocus", cancellable = true)
    public void onNotInFocusTail(CallbackInfo info) {
        if (callGameFocusEvent(EventTiming.POST, false)) info.cancel();
    }
}
