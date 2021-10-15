package net.sushiclient.client.mixin;

import com.google.common.base.Predicate;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.render.HurtCameraEffectEvent;
import net.sushiclient.client.events.render.ItemActivationRenderEvent;
import net.sushiclient.client.events.world.EntityTraceEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;getEntitiesInAABBexcluding(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;Lcom/google/common/base/Predicate;)Ljava/util/List;"), method = "getMouseOver")
    public List<Entity> getEntitiesInAABBExcluding(WorldClient worldClient, Entity entityIn, AxisAlignedBB boundingBox, Predicate<? super Entity> predicate) {
        List<Entity> original = worldClient.getEntitiesInAABBexcluding(entityIn, boundingBox, predicate);
        EntityTraceEvent event = new EntityTraceEvent(worldClient, original);
        EventHandlers.callEvent(event);
        return event.getEntities();
    }

    @Inject(at = @At("HEAD"), method = "hurtCameraEffect", cancellable = true)
    public void hurtCameraEffect(float partialTicks, CallbackInfo ci) {
        HurtCameraEffectEvent event = new HurtCameraEffectEvent();
        EventHandlers.callEvent(event);
        if (event.isCancelled()) ci.cancel();
    }

    @Inject(at = @At("HEAD"), method = "displayItemActivation", cancellable = true)
    public void displayItemActivation(ItemStack stack, CallbackInfo ci) {
        ItemActivationRenderEvent event = new ItemActivationRenderEvent(stack);
        EventHandlers.callEvent(event);
        if (event.isCancelled()) ci.cancel();
    }
}
