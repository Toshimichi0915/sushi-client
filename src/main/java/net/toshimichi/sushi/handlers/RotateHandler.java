package net.toshimichi.sushi.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.CPacketPlayer;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.packet.PacketSendEvent;
import net.toshimichi.sushi.events.tick.ClientTickEvent;
import net.toshimichi.sushi.utils.RotateUtils;

public class RotateHandler {

    private float lastYaw;
    private float lastPitch;

    private boolean update(float yaw, float pitch) {
        if (yaw == lastYaw && pitch == lastPitch) return false;
        lastYaw = yaw;
        lastPitch = pitch;
        return true;
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onPositionPacket(PacketSendEvent e) {
        if (!(e.getPacket() instanceof CPacketPlayer)) return;

        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null) return;

        if (RotateUtils.isSync()) return;
        if (!update(RotateUtils.getYaw(), RotateUtils.getPitch())) return;
        CPacketPlayer cp = (CPacketPlayer) e.getPacket();
        CPacketPlayer.PositionRotation newPacket =
                new CPacketPlayer.PositionRotation(cp.getX(player.posX), cp.getY(player.posY), cp.getZ(player.posZ), lastYaw, lastPitch, cp.isOnGround());
        e.setPacket(newPacket);
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onClientTick(ClientTickEvent e) {
        if (RotateUtils.isSync()) return;

        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null) return;

        NetHandlerPlayClient connection = Minecraft.getMinecraft().getConnection();
        if (connection == null) return;

        if (!update(RotateUtils.getYaw(), RotateUtils.getPitch())) return;
        CPacketPlayer.PositionRotation packet = new CPacketPlayer.PositionRotation(player.posX, player.posY, player.posZ, lastYaw, lastPitch, player.onGround);
        connection.sendPacket(packet);
    }
}
