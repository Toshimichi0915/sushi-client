package net.toshimichi.sushi.mixin;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.player.PlayerTravelEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityPlayer.class)
abstract public class MixinEntityPlayer extends EntityLivingBase {

    public MixinEntityPlayer(World worldIn) {
        super(worldIn);
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;travel(FFF)V"), method = "travel")
    public void onTravel(EntityLivingBase entityLivingBase, float strafe, float vertical, float forward) {
        if (!((Object) this instanceof EntityPlayerSP)) return;
        PlayerTravelEvent pre = new PlayerTravelEvent(EventTiming.PRE, strafe, vertical, forward);
        EventHandlers.callEvent(pre);
        if (pre.isCancelled()) return;
        strafe = pre.getStrafe();
        vertical = pre.getVertical();
        forward = pre.getForward();
        super.travel(strafe, vertical, forward);
        EventHandlers.callEvent(new PlayerTravelEvent(EventTiming.POST, strafe, vertical, forward));
    }
}
