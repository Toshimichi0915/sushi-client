package net.toshimichi.sushi.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.Vec3d;

public class PositionUtils {

    private static SyncMode mode = SyncMode.BOTH;
    private static double x;
    private static double y;
    private static double z;
    private static float yaw;
    private static float pitch;

    public static SyncMode getSyncMode() {
        return mode;
    }

    public static double getX() {
        return x;
    }

    public static double getY() {
        return y;
    }

    public static double getZ() {
        return z;
    }

    public static float getYaw() {
        return yaw;
    }

    public static float getPitch() {
        return pitch;
    }

    public static void setSyncMode(SyncMode mode) {
        PositionUtils.mode = mode;
    }

    public static void move(double x, double y, double z, float yaw, float pitch, boolean position, boolean rotation) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null) return;
        boolean positionPacket = false;
        boolean rotationPacket = false;
        if (position) {
            if (mode.isSyncPosition()) {
                player.setLocationAndAngles(x, y, z, player.rotationYaw, player.rotationPitch);
            } else {
                PositionUtils.x = x;
                PositionUtils.y = y;
                PositionUtils.z = z;
                positionPacket = true;
            }
        }
        if (rotation) {
            if (mode.isSyncLook()) {
                player.rotationYaw = yaw;
                player.rotationPitch = pitch;
            } else {
                PositionUtils.yaw = yaw;
                PositionUtils.pitch = pitch;
                rotationPacket = true;
            }
        }
        CPacketPlayer packet;
        if (positionPacket && rotationPacket) {
            packet = new CPacketPlayer.PositionRotation(getX(), getY(), getZ(), getYaw(), getPitch(), player.onGround);
        } else if (positionPacket) {
            packet = new CPacketPlayer.Position(getX(), getY(), getZ(), player.onGround);
        } else if (rotationPacket) {
            packet = new CPacketPlayer.Rotation(getYaw(), getPitch(), player.onGround);
        } else {
            return;
        }
        NetHandlerPlayClient connection = Minecraft.getMinecraft().getConnection();
        if (connection == null) return;
        connection.sendPacket(packet);
    }

    public static void lookAt(Vec3d loc) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null) return;
        Vec3d origin = new Vec3d(1, 0, 0);
        Vec3d direction = new Vec3d(player.posX, player.posY, player.posZ).subtract(loc).normalize();
        float pitch = (float) (Math.atan2(direction.z, direction.x - 1) * 180 / Math.PI);
        float yaw = (float) (Math.asin(direction.y - origin.y) * 180 / Math.PI);
        move(0, 0, 0, yaw, pitch, false, true);
    }
}