package net.sushiclient.client.utils.player;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class MovementUtils {

    public static Vec3d getMoveInputs(EntityPlayerSP player) {
        float moveUpward = 0;
        if (player.movementInput.jump) moveUpward++;
        if (player.movementInput.sneak) moveUpward--;
        float moveForward = 0;
        float moveStrafe = 0;
        moveForward += player.movementInput.forwardKeyDown ? 1 : 0;
        moveForward -= player.movementInput.backKeyDown ? 1 : 0;
        moveStrafe += player.movementInput.leftKeyDown ? 1 : 0;
        moveStrafe -= player.movementInput.rightKeyDown ? 1 : 0;
        return new Vec3d(moveForward, moveUpward, moveStrafe);
    }

    public static Vec2f toWorld(Vec2f vec, float yaw) {
        if (vec.x == 0 && vec.y == 0) return new Vec2f(0, 0);
        float r = MathHelper.sqrt(vec.x * vec.x + vec.y * vec.y);
        float yawCos = MathHelper.cos((float) (yaw * Math.PI / 180));
        float yawSin = MathHelper.sin((float) (yaw * Math.PI / 180));
        float vecCos = vec.x / r;
        float vecSin = vec.y / r;
        float cos = yawCos * vecCos + yawSin * vecSin;
        float sin = yawSin * vecCos - vecSin * yawCos;
        return new Vec2f(-r * sin, r * cos);
    }
}
