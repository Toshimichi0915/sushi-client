package net.sushiclient.client.utils.player;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.sushiclient.client.utils.MathUtils;
import net.sushiclient.client.utils.world.BlockPlaceInfo;

import java.util.HashMap;
import java.util.Map;

public class PositionUtils {

    private static final float EPSILON = 0.00001F;
    private static final Map<DesyncCloseable, DesyncMode> desync = new HashMap<>();
    private static DesyncMode mode = DesyncMode.NONE;
    private static double x;
    private static double y;
    private static double z;
    private static float yaw;
    private static float pitch;
    private static boolean onGround;

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

    public static boolean isOnGround() {
        return onGround;
    }

    private static synchronized void updateDesyncMode() {
        boolean position = false;
        boolean rotation = false;
        boolean onGround = false;
        for (DesyncMode mode : desync.values()) {
            position = position || mode.isPositionDesync();
            rotation = rotation || mode.isRotationDesync();
            onGround = onGround || mode.isOnGroundDesync();
        }
        mode = new DesyncMode(position, rotation, onGround);
    }

    public static synchronized DesyncCloseable desync(DesyncMode newMode) {
        DesyncCloseable closeable = new DesyncCloseable();
        desync.put(closeable, newMode);
        updateDesyncMode();
        return closeable;
    }

    protected static synchronized void pop(DesyncCloseable closeable) {
        desync.remove(closeable);
        updateDesyncMode();
    }

    public static synchronized void move(Vec3d pos, float yaw, float pitch, boolean position, boolean rotation, DesyncMode mode) {
        move(pos.x, pos.y, pos.z, yaw, pitch, position, rotation, mode);
    }

    public static synchronized void move(double x, double y, double z, float yaw, float pitch, boolean position, boolean rotation, DesyncMode mode) {
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
                if (getDesyncMode().isPositionDesync()) positionPacket = true;
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
                if (getDesyncMode().isRotationDesync()) rotationPacket = true;
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

    public static synchronized void lookAt(Vec3d loc, DesyncMode mode) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null) return;
        Vec3d direction = loc.subtract(new Vec3d(player.posX, player.posY + player.eyeHeight, player.posZ)).normalize();
        if (MathUtils.absMinus(direction.y, 1) < EPSILON) {
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
