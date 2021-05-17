package net.toshimichi.sushi.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.MoverType;
import net.minecraft.util.MovementInput;
import net.minecraft.world.World;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.input.InputUpdateEvent;
import net.toshimichi.sushi.events.player.PlayerMotionEvent;
import net.toshimichi.sushi.events.player.PlayerUpdateEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP extends AbstractClientPlayer {

    public MixinEntityPlayerSP(World worldIn, GameProfile playerProfile) {
        super(worldIn, playerProfile);
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/AbstractClientPlayer;move(Lnet/minecraft/entity/MoverType;DDD)V"), method = "move")
    public void onMove(AbstractClientPlayer abstractClientPlayer, MoverType type, double x, double y, double z) {
        PlayerMotionEvent pre = new PlayerMotionEvent(EventTiming.PRE, type, x, y, z);
        EventHandlers.callEvent(pre);
        if (!pre.isCancelled()) {
            super.move(pre.getType(), pre.getX(), pre.getY(), pre.getZ());
        }
        PlayerMotionEvent post = new PlayerMotionEvent(EventTiming.POST, type, x, y, z);
        EventHandlers.callEvent(post);
    }

    @Inject(at = @At("HEAD"), method = "onUpdateWalkingPlayer", cancellable = true)
    public void onPreUpdateWalkingPlayer(CallbackInfo ci) {
        PlayerUpdateEvent event = new PlayerUpdateEvent(EventTiming.PRE);
        EventHandlers.callEvent(event);
        if (event.isCancelled())
            ci.cancel();
    }

    @Inject(at = @At("TAIL"), method = "onUpdateWalkingPlayer")
    public void onPostUpdateWalkingPlayer(CallbackInfo ci) {
        PlayerUpdateEvent event = new PlayerUpdateEvent(EventTiming.POST);
        EventHandlers.callEvent(event);
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/util/MovementInput;updatePlayerMoveState()V"), method = "onLivingUpdate")
    public void onUpdatePlayerMoveState(MovementInput movementInput) {
        InputUpdateEvent pre = new InputUpdateEvent(EventTiming.PRE);
        EventHandlers.callEvent(pre);
        if (!pre.isCancelled()) movementInput.updatePlayerMoveState();
        InputUpdateEvent post = new InputUpdateEvent(EventTiming.POST);
        EventHandlers.callEvent(post);
    }
}
