package net.sushiclient.client.utils.player;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.CPacketPlayer;

public class PlayerUtils {

    public static CPacketPlayer newCPacketPlayer(CPacketPlayer cp, double x, double y, double z,
                                                 float yaw, float pitch, boolean onGround, boolean position, boolean rotation, boolean flying) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (!position) {
            x = cp.getX(player.posX);
            y = cp.getY(player.posY);
            z = cp.getZ(player.posZ);
        }
        if (!rotation) {
            yaw = cp.getYaw(player.rotationYaw);
            pitch = cp.getPitch(player.rotationPitch);
        }
        if (!flying) onGround = cp.isOnGround();
        if ((position && rotation || cp instanceof CPacketPlayer.PositionRotation) ||
                (position && cp instanceof CPacketPlayer.Rotation) ||
                (rotation && cp instanceof CPacketPlayer.Position)) {
            return new CPacketPlayer.PositionRotation(x, y, z, yaw, pitch, onGround);
        } else if (position || cp instanceof CPacketPlayer.Position) {
            return new CPacketPlayer.Position(x, y, z, onGround);
        } else if (rotation || cp instanceof CPacketPlayer.Rotation) {
            return new CPacketPlayer.Rotation(yaw, pitch, onGround);
        } else {
            if (cp.isOnGround() == onGround) return cp;
            else return new CPacketPlayer(onGround);
        }
    }
}
