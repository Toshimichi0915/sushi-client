package net.toshimichi.sushi.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class RotateUtils {

    private static boolean sync;
    private static float yaw;
    private static float pitch;

    public static boolean isSync() {
        return sync;
    }

    public static float getYaw() {
        return yaw;
    }

    public static float getPitch() {
        return pitch;
    }

    public static void setSync(boolean sync) {
        RotateUtils.sync = sync;
    }

    public static void rotate(float yaw, float pitch) {
        RotateUtils.yaw = MathHelper.wrapDegrees(yaw);
        RotateUtils.pitch = MathHelper.wrapDegrees(pitch);
    }

    public static void rotate(Vec3d loc) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null) return;
        Vec3d origin = new Vec3d(1, 0, 0);
        Vec3d direction = new Vec3d(player.posX, player.posY, player.posZ).subtract(loc).normalize();
        float pitch = (float) (Math.atan2(direction.z, direction.x - 1) * 180 / Math.PI);
        float yaw = (float) (Math.asin(direction.y - origin.y) * 180 / Math.PI);
        rotate(yaw, pitch);
    }
}
