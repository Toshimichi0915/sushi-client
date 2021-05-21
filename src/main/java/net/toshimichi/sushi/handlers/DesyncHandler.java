package net.toshimichi.sushi.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.CPacketPlayer;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.packet.PacketSendEvent;
import net.toshimichi.sushi.utils.DesyncMode;
import net.toshimichi.sushi.utils.PlayerUtils;
import net.toshimichi.sushi.utils.PositionUtils;

public class DesyncHandler {

    @EventHandler(timing = EventTiming.PRE, priority = 8000)
    public void onPositionPacket(PacketSendEvent e) {
        if (!(e.getPacket() instanceof CPacketPlayer)) return;

        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null) return;

        CPacketPlayer cp = (CPacketPlayer) e.getPacket();
        DesyncMode mode = PositionUtils.getDesyncMode();
        boolean position = mode.isPositionDesync();
        boolean rotation = mode.isRotationDesync();
        boolean flying = mode.isOnGroundDesync();
        double x = PositionUtils.getX();
        double y = PositionUtils.getY();
        double z = PositionUtils.getZ();
        float yaw = PositionUtils.getYaw();
        float pitch = PositionUtils.getPitch();
        boolean onGround = PositionUtils.isOnGround();
        e.setPacket(PlayerUtils.newCPacketPlayer(cp, x, y, z, yaw, pitch, onGround, position, rotation, flying));
    }
}
