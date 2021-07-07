package net.toshimichi.sushi.utils.player;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class SpeedUtils {

    public static double getMpt(Entity entity) {
        Vec3d offset = entity.getPositionVector().subtract(new Vec3d(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ));
        return offset.distanceTo(Vec3d.ZERO);
    }

    public static double getMps(Entity entity) {
        return getMpt(entity) * 20;
    }

    public static double getKmph(Entity entity) {
        return getMps(entity) * 60 * 60 / 1000;
    }
}
