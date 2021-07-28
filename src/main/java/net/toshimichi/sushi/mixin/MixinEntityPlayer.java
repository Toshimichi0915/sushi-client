package net.toshimichi.sushi.mixin;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.player.PlayerTickEvent;
import net.toshimichi.sushi.events.player.PlayerTravelEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayer.class)
abstract public class MixinEntityPlayer extends EntityLivingBase {

    public MixinEntityPlayer(World worldIn) {
        super(worldIn);
    }

    @Inject(at = @At("HEAD"), method = "onUpdate", cancellable = true)
    public void onPrePlayerUpdate(CallbackInfo ci) {
        PlayerTickEvent event = new PlayerTickEvent(EventTiming.PRE);
        EventHandlers.callEvent(event);
        if (event.isCancelled()) ci.cancel();
    }

    @Inject(at = @At("TAIL"), method = "onUpdate")
    public void onPostPlayerUpdate(CallbackInfo ci) {
        PlayerTickEvent event = new PlayerTickEvent(EventTiming.POST);
        EventHandlers.callEvent(event);
    }

    @Inject(at = @At("HEAD"), method = "travel", cancellable = true)
    public void onPreTravel(float strafe, float vertical, float forward, CallbackInfo ci) {
        if (!((Object) this instanceof EntityPlayerSP)) return;
        PlayerTravelEvent pre = new PlayerTravelEvent(EventTiming.PRE, strafe, vertical, forward);
        EventHandlers.callEvent(pre);
        if (pre.isCancelled()) ci.cancel();
    }

    @Inject(at = @At("TAIL"), method = "travel")
    public void onPostTravel(float strafe, float vertical, float forward, CallbackInfo ci) {
        if (!((Object) this instanceof EntityPlayerSP)) return;
        EventHandlers.callEvent(new PlayerTravelEvent(EventTiming.POST, strafe, vertical, forward));
    }
}
