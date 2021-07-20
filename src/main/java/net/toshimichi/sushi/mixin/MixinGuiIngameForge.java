package net.toshimichi.sushi.mixin;

import net.minecraftforge.client.GuiIngameForge;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.render.OverlayRenderEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngameForge.class)
public class MixinGuiIngameForge {

    @Inject(at = @At("HEAD"), method = "renderGameOverlay")
    public void onPreRenderGameOverlay(float partialTicks, CallbackInfo info) {
        EventHandlers.callEvent(new OverlayRenderEvent(EventTiming.PRE));
    }

    @Inject(at = @At("TAIL"), method = "renderGameOverlay")
    public void onPostRenderGameOverlay(float partialTicks, CallbackInfo info) {
        EventHandlers.callEvent(new OverlayRenderEvent(EventTiming.POST));
    }
}
