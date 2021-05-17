package net.toshimichi.sushi.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.CPacketPlayer;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.packet.PacketSendEvent;
import net.toshimichi.sushi.utils.DesyncMode;
import net.toshimichi.sushi.utils.PositionUtils;

public class DesyncHandler {

    @EventHandler(timing = EventTiming.PRE)
    public void onPositionPacket(PacketSendEvent e) {
        if (!(e.getPacket() instanceof CPacketPlayer)) return;

        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null) return;

        CPacketPlayer cp = (CPacketPlayer) e.getPacket();
        DesyncMode mode = PositionUtils.getDesyncMode();
        boolean position = mode.isPositionDesync();
        boolean rotation = mode.isRotationDesync();
        double x = PositionUtils.getX(player, mode);
        double y = PositionUtils.getY(player, mode);
        double z = PositionUtils.getZ(player, mode);
        float yaw = PositionUtils.getYaw(player, mode);
        float pitch = PositionUtils.getPitch(player, mode);
        boolean onGround = cp.isOnGround();
        if ((position && rotation || cp instanceof CPacketPlayer.PositionRotation) ||
                (position && cp instanceof CPacketPlayer.Rotation) ||
                (rotation && cp instanceof CPacketPlayer.Position)) {
            e.setPacket(new CPacketPlayer.PositionRotation(x, y, z, yaw, pitch, onGround));
        } else if (position || cp instanceof CPacketPlayer.Position) {
            e.setPacket(new CPacketPlayer.Position(x, y, z, onGround));
        } else if (rotation || cp instanceof CPacketPlayer.Rotation) {
            e.setPacket(new CPacketPlayer.Rotation(yaw, pitch, onGround));
        }
    }
}
