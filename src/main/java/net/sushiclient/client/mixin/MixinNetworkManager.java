package net.sushiclient.client.mixin;

import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.client.ExceptionCatchEvent;
import net.sushiclient.client.events.packet.PacketReceiveEvent;
import net.sushiclient.client.events.packet.PacketSendEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetworkManager.class)
public abstract class MixinNetworkManager {

    @ModifyVariable(at = @At(value = "HEAD", ordinal = 0), method = "sendPacket(Lnet/minecraft/network/Packet;)V")
    public Packet<?> onModifySendPacket(Packet<?> packet) {
        PacketSendEvent event = new PacketSendEvent(packet);
        EventHandlers.callEvent(event);
        if (event.isCancelled()) return null;
        else return event.getPacket();
    }

    @Inject(at = @At("HEAD"), method = "sendPacket(Lnet/minecraft/network/Packet;)V", cancellable = true)
    public void onSendPacket(Packet<?> packet, CallbackInfo info) {
        if (packet == null) info.cancel();
    }

    @Inject(at = @At("HEAD"), method = "exceptionCaught", cancellable = true)
    public void onPreExceptionCaught(ChannelHandlerContext context, Throwable throwable, CallbackInfo ci) {
        ExceptionCatchEvent event = new ExceptionCatchEvent(throwable);
        EventHandlers.callEvent(event);
        if (event.isCancelled()) ci.cancel();
    }

    @ModifyVariable(at = @At(value = "HEAD", ordinal = 0), method = "channelRead0")
    public Packet<?> onModifyChannel0(Packet<?> packetIn) {
        PacketReceiveEvent event = new PacketReceiveEvent(packetIn);
        EventHandlers.callEvent(event);
        return event.isCancelled() ? null : event.getPacket();
    }

    @Inject(at = @At("HEAD"), method = "channelRead0", cancellable = true)
    public void onChannelRead0(ChannelHandlerContext context, Packet<?> packetIn, CallbackInfo ci) {
        if (packetIn == null) ci.cancel();
    }

}
