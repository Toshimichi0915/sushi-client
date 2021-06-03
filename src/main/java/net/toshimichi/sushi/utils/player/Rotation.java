package net.toshimichi.sushi.utils.player;

import net.minecraft.util.math.Vec3d;

public class Rotation {
    private final float yaw;
    private final float pitch;

    public Rotation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Rotation(Vec3d direction) {
        Vec3d origin = new Vec3d(1, 0, 0);
        this.yaw = (float) (Math.asin(direction.y - origin.y) * 180 / Math.PI);
        this.pitch = (float) (Math.atan2(direction.z, direction.x - 1) * 180 / Math.PI);
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }
}
