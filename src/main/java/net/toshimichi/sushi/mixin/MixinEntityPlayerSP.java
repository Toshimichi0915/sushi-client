package net.toshimichi.sushi.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.MoverType;
import net.minecraft.world.World;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.player.PlayerMoveEvent;
import net.toshimichi.sushi.events.player.PlayerPacketEvent;
import net.toshimichi.sushi.events.player.PlayerPushOutOfBlocksEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP extends AbstractClientPlayer {

    public MixinEntityPlayerSP(World worldIn, GameProfile playerProfile) {
        super(worldIn, playerProfile);
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/AbstractClientPlayer;move(Lnet/minecraft/entity/MoverType;DDD)V"), method = "move")
    public void onMove(AbstractClientPlayer abstractClientPlayer, MoverType type, double x, double y, double z) {
        PlayerMoveEvent pre = new PlayerMoveEvent(EventTiming.PRE, type, x, y, z);
        EventHandlers.callEvent(pre);
        if (!pre.isCancelled()) {
            super.move(pre.getType(), pre.getX(), pre.getY(), pre.getZ());
        }
        PlayerMoveEvent post = new PlayerMoveEvent(EventTiming.POST, type, x, y, z);
        EventHandlers.callEvent(post);
    }

    @Inject(at = @At("HEAD"), method = "onUpdateWalkingPlayer", cancellable = true)
    public void onPreUpdateWalkingPlayer(CallbackInfo ci) {
        PlayerPacketEvent event = new PlayerPacketEvent(EventTiming.PRE);
        EventHandlers.callEvent(event);
        if (event.isCancelled())
            ci.cancel();
    }

    @Inject(at = @At("TAIL"), method = "onUpdateWalkingPlayer")
    public void onPostUpdateWalkingPlayer(CallbackInfo ci) {
        PlayerPacketEvent event = new PlayerPacketEvent(EventTiming.POST);
        EventHandlers.callEvent(event);
    }

    @Inject(at = @At("HEAD"), method = "pushOutOfBlocks", cancellable = true)
    public void onPushOutBlocks(double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        PlayerPushOutOfBlocksEvent e = new PlayerPushOutOfBlocksEvent(x, y, z);
        EventHandlers.callEvent(e);
        if(e.isCancelled()) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
