package net.toshimichi.sushi.mixin;

import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.PacketReceiveEvent;
import net.toshimichi.sushi.events.PacketSendEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetworkManager.class)
public class MixinNetworkManager {

    @Inject(at = @At("HEAD"), method = "sendPacket(Lnet/minecraft/network/Packet;)V", cancellable = true)
    public void onSendPacket(Packet<?> packet, CallbackInfo info) {
        PacketSendEvent event = new PacketSendEvent(packet);
        EventHandlers.callEvent(event);
        if(event.isCancelled()) {
            info.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "channelRead0", cancellable = true)
    public void onReceivePacket(ChannelHandlerContext context, Packet<?> packet, CallbackInfo info) {
        PacketReceiveEvent event = new PacketReceiveEvent(packet);
        EventHandlers.callEvent(event);
        if(event.isCancelled()) {
            info.cancel();
        }
    }
}
