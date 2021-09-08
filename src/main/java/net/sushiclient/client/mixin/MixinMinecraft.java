package net.sushiclient.client.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.client.GameFocusEvent;
import net.sushiclient.client.events.client.WorldLoadEvent;
import net.sushiclient.client.events.tick.GameTickEvent;
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
    public void onPreFocus(CallbackInfo info) {
        if (callGameFocusEvent(EventTiming.PRE, true)) info.cancel();
    }

    @Inject(at = @At("HEAD"), method = "setIngameNotInFocus", cancellable = true)
    public void onPreNotInFocus(CallbackInfo info) {
        if (callGameFocusEvent(EventTiming.PRE, false)) info.cancel();
    }

    @Inject(at = @At("TAIL"), method = "setIngameFocus")
    public void onPostFocus(CallbackInfo info) {
        callGameFocusEvent(EventTiming.POST, true);
    }

    @Inject(at = @At("TAIL"), method = "setIngameNotInFocus")
    public void onPostNotInFocus(CallbackInfo info) {
        callGameFocusEvent(EventTiming.POST, false);
    }

    @Inject(at = @At("HEAD"), method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V")
    public void onPreLoadWorld(WorldClient client, String loadingMessage, CallbackInfo ci) {
        EventHandlers.callEvent(new WorldLoadEvent(EventTiming.PRE, client));
    }

    @Inject(at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/util/Timer;updateTimer()V"), method = "runGameLoop")
    public void updateTimer(CallbackInfo ci) {
        GameTickEvent pre = new GameTickEvent(EventTiming.PRE);
        EventHandlers.callEvent(pre);
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;updateDisplay()V"), method = "runGameLoop")
    public void updateDisplay(CallbackInfo ci) {
        GameTickEvent post = new GameTickEvent(EventTiming.POST);
        EventHandlers.callEvent(post);
    }

    @Inject(at = @At("TAIL"), method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V")
    public void onPostLoadWorld(WorldClient client, String loadingMessage, CallbackInfo info) {
        EventHandlers.callEvent(new WorldLoadEvent(EventTiming.POST, client));
    }
}