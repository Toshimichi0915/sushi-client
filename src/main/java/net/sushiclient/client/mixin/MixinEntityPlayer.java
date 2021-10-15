package net.sushiclient.client.mixin;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.player.PlayerTickEvent;
import net.sushiclient.client.events.player.PlayerTravelEvent;
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
    public void preOnUpdate(CallbackInfo ci) {
        PlayerTickEvent event = new PlayerTickEvent(EventTiming.PRE);
        EventHandlers.callEvent(event);
        if (event.isCancelled()) ci.cancel();
    }

    @Inject(at = @At("TAIL"), method = "onUpdate")
    public void postOnUpdate(CallbackInfo ci) {
        PlayerTickEvent event = new PlayerTickEvent(EventTiming.POST);
        EventHandlers.callEvent(event);
    }

    @Inject(at = @At("HEAD"), method = "travel", cancellable = true)
    public void preTravel(float strafe, float vertical, float forward, CallbackInfo ci) {
        if (!((Object) this instanceof EntityPlayerSP)) return;
        PlayerTravelEvent pre = new PlayerTravelEvent(EventTiming.PRE, strafe, vertical, forward);
        EventHandlers.callEvent(pre);
        if (pre.isCancelled()) ci.cancel();
    }

    @Inject(at = @At("TAIL"), method = "travel")
    public void postTravel(float strafe, float vertical, float forward, CallbackInfo ci) {
        if (!((Object) this instanceof EntityPlayerSP)) return;
        EventHandlers.callEvent(new PlayerTravelEvent(EventTiming.POST, strafe, vertical, forward));
    }
}
