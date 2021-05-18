package net.toshimichi.sushi.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.util.IThreadListener;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.packet.PacketReceiveEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PacketThreadUtil.class)
public class MixinPacketThreadUtil {
    @Inject(at = @At("HEAD"), method = "checkThreadAndEnqueue", cancellable = true)
    private static void checkThreadAndEnqueue(Packet<INetHandler> packetIn, INetHandler processor, IThreadListener scheduler, CallbackInfo ci) {
        if (!Minecraft.getMinecraft().isCallingFromMinecraftThread()) return;
        PacketReceiveEvent event = new PacketReceiveEvent(packetIn);
        EventHandlers.callEvent(event);
        if (event.isCancelled()) ci.cancel();
    }
}
