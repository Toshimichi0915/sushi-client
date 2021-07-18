package net.toshimichi.sushi.utils.player;

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
        float invert = vec.y > 0 ? 1 : -1;
        float r = MathHelper.sqrt(vec.x * vec.x + vec.y * vec.y);
        float cos1 = MathHelper.cos((float) (yaw * Math.PI / 180));
        float sin1 = MathHelper.sin((float) (yaw * Math.PI / 180));
        float cos2 = vec.x / r;
        float sin2 = MathHelper.sqrt(1 - cos2 * cos2);
        float cos = cos1 * cos2 + sin1 * sin2 * invert;
        float sin = sin1 * cos2 - sin2 * cos1 * invert;
        return new Vec2f(-r * sin, r * cos);
    }
}
