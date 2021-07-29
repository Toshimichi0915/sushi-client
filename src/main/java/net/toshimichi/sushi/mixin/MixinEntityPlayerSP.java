package net.toshimichi.sushi.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.MoverType;
import net.minecraft.world.World;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.player.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP extends AbstractClientPlayer {

    @Shadow
    public abstract void move(MoverType type, double x, double y, double z);

    public MixinEntityPlayerSP(World worldIn, GameProfile playerProfile) {
        super(worldIn, playerProfile);
    }

    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
    public void onMove(MoverType type, double x, double y, double z, CallbackInfo ci) {
        PlayerMoveEvent pre = new PlayerMoveEvent(EventTiming.PRE, type, x, y, z);
        EventHandlers.callEvent(pre);
        boolean changed = pre.getType() != type || pre.getX() != x || pre.getY() != y || pre.getZ() != z;
        if (pre.isCancelled() || changed) {
            ci.cancel();
            if (changed) move(pre.getType(), pre.getX(), pre.getY(), pre.getZ());
            return;
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
        if (e.isCancelled()) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "isCurrentViewEntity", cancellable = true)
    public void onIsCurrentViewEntity(CallbackInfoReturnable<Boolean> cir) {
        boolean currentViewEntity = Minecraft.getMinecraft().getRenderViewEntity() == this;
        CurrentViewEntityCheckEvent event = new CurrentViewEntityCheckEvent(EventTiming.PRE, currentViewEntity);
        EventHandlers.callEvent(event);
        cir.setReturnValue(event.isCurrentViewEntity());
        cir.cancel();
    }

    @Inject(at = @At("HEAD"), method = "isUser", cancellable = true)
    public void onIsUser(CallbackInfoReturnable<Boolean> cir) {
        UserCheckEvent event = new UserCheckEvent(EventTiming.PRE, true);
        EventHandlers.callEvent(event);
        cir.setReturnValue(event.isUser());
        cir.cancel();
    }
}
