package net.toshimichi.sushi.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.CPacketPlayer;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.packet.PacketSendEvent;
import net.toshimichi.sushi.utils.PositionUtils;
import net.toshimichi.sushi.utils.SyncMode;

public class RotateHandler {

    @EventHandler(timing = EventTiming.PRE)
    public void onPositionPacket(PacketSendEvent e) {
        if (!(e.getPacket() instanceof CPacketPlayer)) return;

        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null) return;

        CPacketPlayer cp = (CPacketPlayer) e.getPacket();
        SyncMode mode = PositionUtils.getSyncMode();
        boolean position = !mode.isSyncPosition();
        boolean rotation = !mode.isSyncLook();
        double x = position ? PositionUtils.getX() : cp.getX(player.posX);
        double y = position ? PositionUtils.getY() : cp.getY(player.posY);
        double z = position ? PositionUtils.getZ() : cp.getZ(player.posZ);
        float yaw = rotation ? PositionUtils.getYaw() : cp.getYaw(player.rotationYaw);
        float pitch = rotation ? PositionUtils.getPitch() : cp.getPitch(player.rotationPitch);
        boolean onGround = cp.isOnGround();
        if (position && rotation || cp instanceof CPacketPlayer.PositionRotation) {
            e.setPacket(new CPacketPlayer.PositionRotation(x, y, z, yaw, pitch, onGround));
        } else if (position || cp instanceof CPacketPlayer.Position) {
            e.setPacket(new CPacketPlayer.Position(x, y, z, onGround));
        } else if (rotation || cp instanceof CPacketPlayer.Rotation) {
            e.setPacket(new CPacketPlayer.Rotation(yaw, pitch, onGround));
        }
    }
}
