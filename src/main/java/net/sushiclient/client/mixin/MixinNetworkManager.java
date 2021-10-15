package net.sushiclient.client.mixin;

import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
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
    public Packet<?> modifySendPacket(Packet<?> packet) {
        PacketSendEvent event = new PacketSendEvent(EventTiming.PRE, packet);
        EventHandlers.callEvent(event);
        if (event.isCancelled()) return null;
        else return event.getPacket();
    }

    @Inject(at = @At("HEAD"), method = "sendPacket(Lnet/minecraft/network/Packet;)V", cancellable = true)
    public void preSendPacket(Packet<?> packet, CallbackInfo info) {
        if (packet == null) info.cancel();
    }

    @Inject(at = @At("TAIL"), method = "sendPacket(Lnet/minecraft/network/Packet;)V")
    public void postSendPacket(Packet<?> packet, CallbackInfo info) {
        PacketSendEvent event = new PacketSendEvent(EventTiming.POST, packet);
        EventHandlers.callEvent(event);
    }

    @Inject(at = @At("HEAD"), method = "exceptionCaught", cancellable = true)
    public void exceptionCaught(ChannelHandlerContext context, Throwable throwable, CallbackInfo ci) {
        ExceptionCatchEvent event = new ExceptionCatchEvent(throwable);
        EventHandlers.callEvent(event);
        if (event.isCancelled()) ci.cancel();
    }

    @ModifyVariable(at = @At(value = "HEAD", ordinal = 0), method = "channelRead0")
    public Packet<?> modifyChannel0(Packet<?> packetIn) {
        PacketReceiveEvent event = new PacketReceiveEvent(EventTiming.PRE, packetIn);
        EventHandlers.callEvent(event);
        return event.isCancelled() ? null : event.getPacket();
    }

    @Inject(at = @At("HEAD"), method = "channelRead0", cancellable = true)
    public void preChannelRead0(ChannelHandlerContext context, Packet<?> packetIn, CallbackInfo ci) {
        if (packetIn == null) ci.cancel();
    }

    @Inject(at = @At("TAIL"), method = "channelRead0")
    public void postChannelRead0(ChannelHandlerContext context, Packet<?> packetIn, CallbackInfo ci) {
        PacketReceiveEvent event = new PacketReceiveEvent(EventTiming.POST, packetIn);
        EventHandlers.callEvent(event);
    }
}
