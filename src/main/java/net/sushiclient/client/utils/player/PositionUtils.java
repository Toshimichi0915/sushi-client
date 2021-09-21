package net.sushiclient.client.utils.player;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.math.Vec3d;
import net.sushiclient.client.mixin.AccessorEntityPlayerSP;
import net.sushiclient.client.utils.MathUtils;
import net.sushiclient.client.utils.world.BlockPlaceInfo;
import net.sushiclient.client.utils.world.BlockUtils;

import java.util.ArrayList;
import java.util.HashSet;

public class PositionUtils {

    private static final float EPSILON = 0.00001F;
    private static final ArrayList<AutoDesyncOperator> desync = new ArrayList<>();
    private static final HashSet<AutoDesyncOperator> temp = new HashSet<>();
    private static final ArrayList<Runnable> runnables = new ArrayList<>();
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
        for (AutoDesyncOperator operator : desync) {
            DesyncMode mode = operator.getDesyncMode();
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
        mode = new DesyncMode(position, rotation, ground);
        desync.removeAll(temp);
        temp.clear();
    }

    public static void updatePositionUpdateTicks() {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null) return;
        ((AccessorEntityPlayerSP) player).setPositionUpdateTicks(20);
    }

    public static DesyncOperator desync() {
        DesyncOperator closeable = new DesyncOperator();
        desync.add(closeable);
        return closeable;
    }

    public static AutoDesyncOperator require() {
        AutoDesyncOperator operator = new DesyncOperator();
        desync.add(operator);
        temp.add(operator);
        return operator;
    }

    protected static void pop(AutoDesyncOperator closeable) {
        desync.remove(closeable);
    }

    protected static void move(double x, double y, double z, float yaw, float pitch, boolean onGround,
                               DesyncMode mode, AutoDesyncOperator operator) {
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

    public static void move(Vec3d pos, float yaw, float pitch, boolean onGround, DesyncMode mode) {
        move(pos.x, pos.y, pos.z, yaw, pitch, onGround, mode);
    }

    public static void move(double x, double y, double z, float yaw, float pitch, boolean onGround, DesyncMode mode) {
        move(x, y, z, yaw, pitch, onGround, mode, null);
    }

    protected static void lookAt(Vec3d loc, DesyncMode mode, AutoDesyncOperator operator) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null) return;
        Vec3d direction = loc.subtract(new Vec3d(player.posX, player.posY + player.eyeHeight, player.posZ)).normalize();
        if (MathUtils.absMinus(direction.y, 1) < EPSILON) {
            // workaround for Math#asin returning Double.NaN
            direction = new Vec3d(direction.x, Math.signum(direction.y) * (1 - EPSILON), direction.z);
        }
        float yaw = (float) (Math.atan2(direction.z, direction.x) * 180 / Math.PI) - 90;
        float pitch = (float) -(Math.asin(direction.y) * 180 / Math.PI);
        move(0, 0, 0, yaw, pitch, true, mode, operator);
    }

    public static void lookAt(Vec3d loc, DesyncMode mode) {
        lookAt(loc, mode, null);
    }

    protected static void lookAt(BlockPlaceInfo info, AutoDesyncOperator operator) {
        Vec3d pos = BlockUtils.toVec3d(info.getBlockPos());
        lookAt(info.getBlockFace().getPos().add(pos).add(BlockUtils.toVec3d(info.getBlockFace().getFacing().getOpposite().getDirectionVec())), DesyncMode.LOOK, operator);
    }

    public static void lookAt(BlockPlaceInfo info) {
        lookAt(info, null);
    }

    public static void close(DesyncOperator operator) {
        if (operator != null) operator.close();
    }

    public static void invokeAll() {
        runnables.forEach(Runnable::run);
        runnables.clear();
    }

    public static void on(Runnable runnable) {
        runnables.add(runnable);
    }
}
