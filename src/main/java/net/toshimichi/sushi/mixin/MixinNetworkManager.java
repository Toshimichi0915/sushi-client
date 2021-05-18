package net.toshimichi.sushi.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.packet.PacketSendEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetworkManager.class)
public abstract class MixinNetworkManager {

    @Shadow
    public abstract void sendPacket(Packet<?> packetIn);

    @ModifyVariable(at = @At(value = "HEAD", ordinal = 0), method = "sendPacket(Lnet/minecraft/network/Packet;)V")
    public Packet<?> onModifySendPacket(Packet<?> packet) {
        if (!Minecraft.getMinecraft().isCallingFromMinecraftThread()) return packet;
        PacketSendEvent event = new PacketSendEvent(packet);
        EventHandlers.callEvent(event);
        if (event.isCancelled())
            return null;
        else
            return event.getPacket();
    }

    @Inject(at = @At("HEAD"), method = "sendPacket(Lnet/minecraft/network/Packet;)V", cancellable = true)
    public void onSendPacket(Packet<?> packet, CallbackInfo info) {
        if (!Minecraft.getMinecraft().isCallingFromMinecraftThread()) {
            Minecraft.getMinecraft().addScheduledTask(() -> sendPacket(packet));
            info.cancel();
            return;
        }
        if (packet == null) {
            info.cancel();
        }
    }

}
