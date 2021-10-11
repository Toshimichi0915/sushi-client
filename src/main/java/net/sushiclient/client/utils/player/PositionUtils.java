package net.sushiclient.client.utils.player;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.math.Vec3d;
import net.sushiclient.client.mixin.AccessorEntityPlayerSP;
import net.sushiclient.client.utils.world.BlockPlaceInfo;
import net.sushiclient.client.utils.world.BlockUtils;

import java.util.ArrayList;
import java.util.HashSet;

public class PositionUtils {

    private static final ArrayList<PositionOperator> desync = new ArrayList<>();
    private static final HashSet<PositionOperator> temp = new HashSet<>();
    private static final ArrayList<Runnable> runnables = new ArrayList<>();
    private static PositionMask mode = PositionMask.NONE;
    private static double x;
    private static double y;
    private static double z;
    private static float yaw;
    private static float pitch;
    private static boolean onGround;

    public static PositionMask getDesyncMode() {
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

    public static void update() {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        double x = player.posX;
        double y = player.posY;
        double z = player.posZ;
        float yaw = player.rotationYaw;
        float pitch = player.rotationPitch;
        boolean onGround = player.onGround;
        boolean position = false;
        boolean rotation = false;
        boolean ground = false;
        for (PositionOperator operator : desync) {
            PositionMask mode = operator.getDesyncMode();
            if (mode.isPositionDesync()) {
                x = operator.getX();
                y = operator.getY();
                z = operator.getZ();
            }
            if (mode.isRotationDesync()) {
                yaw = operator.getYaw();
                pitch = operator.getPitch();
            }
            if (mode.isOnGroundDesync()) {
                onGround = operator.isOnGround();
            }
            position = position || mode.isPositionDesync();
            rotation = rotation || mode.isRotationDesync();
            ground = ground || mode.isOnGroundDesync();
        }
        PositionUtils.x = x;
        PositionUtils.y = y;
        PositionUtils.z = z;
        PositionUtils.yaw = yaw;
        PositionUtils.pitch = pitch;
        PositionUtils.onGround = onGround;
        mode = new PositionMask(position, rotation, ground);
    }

    public static void updatePositionUpdateTicks() {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null) return;
        ((AccessorEntityPlayerSP) player).setPositionUpdateTicks(20);
    }

    public static CloseablePositionOperator desync() {
        CloseablePositionOperator closeable = new CloseablePositionOperator();
        desync.add(closeable);
        return closeable;
    }

    public static PositionOperator require() {
        PositionOperator operator = new CloseablePositionOperator();
        desync.add(operator);
        temp.add(operator);
        return operator;
    }

    protected static void pop(PositionOperator closeable) {
        desync.remove(closeable);
    }

    protected static void move(double x, double y, double z, float yaw, float pitch, boolean onGround,
                               PositionMask mode, PositionOperator operator) {
        if (!Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(z) ||
                !Float.isFinite(yaw) || !Float.isFinite(pitch)) {
            throw new IllegalArgumentException("Invalid movement x: " + x + " y: " + y + " z: " + z +
                    " yaw: " + yaw + " pitch: " + pitch);
        }
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null) return;
        if (mode.isPositionDesync()) {
            if (operator != null) operator.setPos(x, y, z);
            else player.setPosition(x, y, z);
        }
        while (yaw > 180) yaw -= 360;
        while (yaw < -180) yaw += 360;
        while (pitch > 90) pitch -= 180;
        while (pitch < -90) pitch += 180;
        if (mode.isRotationDesync()) {
            if (operator != null) {
                operator.setRotation(yaw, pitch);
            } else {
                player.rotationYaw = yaw;
                player.rotationPitch = pitch;
            }
        }
        if (mode.isOnGroundDesync()) {
            if (operator != null) operator.setOnGround(onGround);
            else player.onGround = onGround;
        }
    }

    public static void move(Vec3d pos, float yaw, float pitch, boolean onGround, PositionMask mode) {
        move(pos.x, pos.y, pos.z, yaw, pitch, onGround, mode);
    }

    public static void move(double x, double y, double z, float yaw, float pitch, boolean onGround, PositionMask mode) {
        move(x, y, z, yaw, pitch, onGround, mode, null);
    }

    protected static void lookAt(Vec3d loc, PositionOperator operator) {
        float[] lookVec = BlockUtils.getLookVec(loc);
        if (lookVec != null) {
            move(0, 0, 0, lookVec[0], lookVec[1], true, PositionMask.LOOK, operator);
        }
    }

    public static void lookAt(Vec3d loc) {
        lookAt(loc, null);
    }

    protected static void lookAt(BlockPlaceInfo info, PositionOperator operator) {
        float[] lookVec = BlockUtils.getLookVec(info);
        if (lookVec != null) {
            move(0, 0, 0, lookVec[0], lookVec[1], true, PositionMask.LOOK, operator);
        }
    }

    public static void lookAt(BlockPlaceInfo info) {
        lookAt(info, null);
    }

    public static void close(CloseablePositionOperator operator) {
        if (operator != null) operator.close();
    }

    public static void invokeAll() {
        ArrayList<Runnable> clone = new ArrayList<>(runnables);
        runnables.clear();
        clone.forEach(Runnable::run);
        desync.removeAll(temp);
        temp.clear();
    }

    public static void on(Runnable runnable) {
        runnables.add(runnable);
    }
}
