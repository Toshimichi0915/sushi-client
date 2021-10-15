package net.sushiclient.client.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.Session;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.client.GameFocusEvent;
import net.sushiclient.client.events.client.WorldLoadEvent;
import net.sushiclient.client.events.render.GuiScreenCloseEvent;
import net.sushiclient.client.events.render.GuiScreenDisplayEvent;
import net.sushiclient.client.events.tick.GameTickEvent;
import net.sushiclient.client.utils.player.SessionUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class MixinMinecraft {

    private boolean callGameFocusEvent(EventTiming timing, boolean focused) {
        GameFocusEvent event = new GameFocusEvent(timing, focused);
        EventHandlers.callEvent(event);
        return event.isCancelled();
    }

    @Inject(at = @At("HEAD"), method = "setIngameFocus", cancellable = true)
    public void preSetIngameFocus(CallbackInfo info) {
        if (callGameFocusEvent(EventTiming.PRE, true)) info.cancel();
    }

    @Inject(at = @At("HEAD"), method = "setIngameNotInFocus", cancellable = true)
    public void preSetIngameNotInFocus(CallbackInfo info) {
        if (callGameFocusEvent(EventTiming.PRE, false)) info.cancel();
    }

    @Inject(at = @At("TAIL"), method = "setIngameFocus")
    public void postSetIngameFocus(CallbackInfo info) {
        callGameFocusEvent(EventTiming.POST, true);
    }

    @Inject(at = @At("TAIL"), method = "setIngameNotInFocus")
    public void postSetIngameNotInFocus(CallbackInfo info) {
        callGameFocusEvent(EventTiming.POST, false);
    }

    @Inject(at = @At("HEAD"), method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V")
    public void preLoadWorld(WorldClient client, String loadingMessage, CallbackInfo ci) {
        EventHandlers.callEvent(new WorldLoadEvent(EventTiming.PRE, client));
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;updateDisplay()V"), method = "runGameLoop")
    public void updateDisplay(CallbackInfo ci) {
        Minecraft.getMinecraft().profiler.startSection("sushiClientGameTick");
        GameTickEvent post = new GameTickEvent(EventTiming.PRE);
        EventHandlers.callEvent(post);
    }

    @Inject(at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/util/Timer;updateTimer()V"), method = "runGameLoop")
    public void updateTimer(CallbackInfo ci) {
        GameTickEvent pre = new GameTickEvent(EventTiming.POST);
        EventHandlers.callEvent(pre);
        Minecraft.getMinecraft().profiler.endSection();
    }

    @Inject(at = @At("TAIL"), method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V")
    public void postLoadWorld(WorldClient client, String loadingMessage, CallbackInfo info) {
        EventHandlers.callEvent(new WorldLoadEvent(EventTiming.POST, client));
    }

    @Inject(at = @At("HEAD"), method = "getSession", cancellable = true)
    public void getSession(CallbackInfoReturnable<Session> cir) {
        Session session = SessionUtils.getSession();
        if (session != null) {
            cir.setReturnValue(session);
            cir.cancel();
        }
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiScreen;onGuiClosed()V"), method = "displayGuiScreen")
    public void onGuiClose(GuiScreen screen) {
        GuiScreenCloseEvent pre = new GuiScreenCloseEvent(screen, EventTiming.PRE);
        EventHandlers.callEvent(pre);
        if (!pre.isCancelled()) {
            screen.onGuiClosed();
            GuiScreenCloseEvent post = new GuiScreenCloseEvent(screen, EventTiming.POST);
            EventHandlers.callEvent(post);
        }
    }


    @Inject(at = @At("TAIL"), method = "displayGuiScreen")
    public void displayGuiScreen(GuiScreen guiScreenIn, CallbackInfo ci) {
        GuiScreenDisplayEvent event = new GuiScreenDisplayEvent(guiScreenIn, EventTiming.POST);
        EventHandlers.callEvent(event);
    }
}
