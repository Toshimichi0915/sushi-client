package net.sushiclient.client.mixin;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.render.LivingLabelRenderEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Render.class)
public class MixinRender {

    @Inject(at = @At("HEAD"), method = "renderLivingLabel", cancellable = true)
    public void renderLivingLabel(Entity entityIn, String str, double x, double y, double z, int maxDistance, CallbackInfo ci) {
        LivingLabelRenderEvent event = new LivingLabelRenderEvent(entityIn, str, x, y, z, maxDistance);
        EventHandlers.callEvent(event);
        if (event.isCancelled()) ci.cancel();
    }
}
