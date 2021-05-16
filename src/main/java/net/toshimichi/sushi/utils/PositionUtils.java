package net.toshimichi.sushi.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Stack;

public class PositionUtils {

    private static final float EPSILON = 0.00001F;
    private static final Stack<DesyncMode> stack = new Stack<>();
    private static DesyncMode mode = DesyncMode.NONE;
    private static double x;
    private static double y;
    private static double z;
    private static float yaw;
    private static float pitch;

    public static DesyncMode getDesyncMode() {
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

    public static double getX(EntityPlayerSP player, DesyncMode mode) {
        return mode.isPositionDesync() ? getX() : player.posX;
    }

    public static double getY(EntityPlayerSP player, DesyncMode mode) {
        return mode.isPositionDesync() ? getY() : player.posY;
    }

    public static double getZ(EntityPlayerSP player, DesyncMode mode) {
        return mode.isPositionDesync() ? getZ() : player.posZ;
    }

    public static float getYaw(EntityPlayerSP player, DesyncMode mode) {
        return mode.isRotationDesync() ? getYaw() : player.rotationYaw;
    }

    public static float getPitch(EntityPlayerSP player, DesyncMode mode) {
        return mode.isRotationDesync() ? getPitch() : player.rotationPitch;
    }

    public static void desync(DesyncMode newMode) {
        boolean position = mode.isPositionDesync() || newMode.isPositionDesync();
        boolean rotation = mode.isRotationDesync() || newMode.isRotationDesync();
        stack.push(mode);
        mode = DesyncMode.valueOf(position, rotation);
    }

    public static void pop() {
        mode = stack.pop();
    }

    public static void move(double x, double y, double z, float yaw, float pitch, boolean position, boolean rotation, DesyncMode mode) {
        if (!Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(z) ||
                !Float.isFinite(yaw) || !Float.isFinite(pitch)) {
            throw new IllegalArgumentException("Invalid movement x: " + x + " y: " + y + " z: " + z +
                    " yaw: " + yaw + " pitch: " + pitch);
        }
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null) return;
        boolean positionPacket = false;
        boolean rotationPacket = false;
        if (position) {
            if (mode.isPositionDesync()) {
                positionPacket = true;
                PositionUtils.x = x;
                PositionUtils.y = y;
                PositionUtils.z = z;
            } else {
                player.setPosition(x, y, z);
            }
        }
        if (rotation) {
            while (yaw > 180) yaw -= 360;
            while (yaw < -180) yaw += 360;
            while (pitch > 90) pitch -= 180;
            while (pitch < -90) pitch += 180;
            if (mode.isRotationDesync()) {
                rotationPacket = true;
                PositionUtils.yaw = yaw;
                PositionUtils.pitch = pitch;
            } else {
                player.rotationYaw = yaw;
                player.rotationPitch = pitch;
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

    public static void lookAt(Vec3d loc, DesyncMode mode) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null) return;
        Vec3d direction = loc.subtract(new Vec3d(player.posX, player.posY + player.eyeHeight, player.posZ)).normalize();
        if (Math.abs(Math.abs(direction.y) - 1) < EPSILON) {
            // workaround for Math#asin returning Double.NaN
            direction = new Vec3d(direction.x, Math.signum(direction.y) * (1 - EPSILON), direction.z);
        }
        float yaw = (float) (Math.atan2(direction.z, direction.x) * 180 / Math.PI) - 90;
        float pitch = (float) -(Math.asin(direction.y) * 180 / Math.PI);
        move(0, 0, 0, yaw, pitch, false, true, mode);
    }

    public static void lookAt(BlockPlaceInfo info, DesyncMode mode) {
        BlockPos pos = info.getBlockPos();
        lookAt(info.getBlockFace().getPos().add(pos.getX(), pos.getY(), pos.getZ()), mode);
    }
}
