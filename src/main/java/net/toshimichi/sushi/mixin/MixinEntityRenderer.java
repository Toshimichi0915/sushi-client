package net.toshimichi.sushi.mixin;

import com.google.common.base.Predicate;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.world.EntityTraceEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {
    @Redirect(method = "getMouseOver", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;getEntitiesInAABBexcluding(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;Lcom/google/common/base/Predicate;)Ljava/util/List;"))
    public List<Entity> getEntitiesInAABBExcluding(WorldClient worldClient, Entity entityIn, AxisAlignedBB boundingBox, Predicate<? super Entity> predicate) {
        List<Entity> original = worldClient.getEntitiesInAABBexcluding(entityIn, boundingBox, predicate);
        EntityTraceEvent event = new EntityTraceEvent(original);
        EventHandlers.callEvent(event);
        return event.getEntities();
    }
}