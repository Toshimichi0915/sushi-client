package net.sushiclient.client.mixin;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.render.EntityRenderEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderManager.class)
public class MixinRenderManager {

    @Inject(at = @At("HEAD"), method = "renderEntity", cancellable = true)
    public void onPreRenderEntity(Entity entityIn, double x, double y, double z, float yaw, float partialTicks, boolean debug, CallbackInfo ci) {
        if (entityIn == null) return;
        EntityRenderEvent event = new EntityRenderEvent(EventTiming.PRE, !(entityIn instanceof EntityLivingBase),
                entityIn, x, y, z, yaw, partialTicks, debug);
        EventHandlers.callEvent(event);
        if (event.isCancelled()) ci.cancel();
    }

    @Inject(at = @At("TAIL"), method = "renderEntity")
    public void onPostRenderEntity(Entity entityIn, double x, double y, double z, float yaw, float partialTicks, boolean debug, CallbackInfo ci) {
        if (entityIn == null) return;
        EntityRenderEvent event = new EntityRenderEvent(EventTiming.POST, !(entityIn instanceof EntityLivingBase),
                entityIn, x, y, z, yaw, partialTicks, debug);
        EventHandlers.callEvent(event);
    }
}
