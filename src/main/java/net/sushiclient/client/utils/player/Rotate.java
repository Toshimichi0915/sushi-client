package net.sushiclient.client.utils.player;

import net.minecraft.util.math.Vec3d;
import net.sushiclient.client.utils.world.BlockPlaceInfo;
import net.sushiclient.client.utils.world.BlockUtils;

public interface Rotate {
    void rotate(float yaw, float pitch, boolean desync, PositionOperator operator, Runnable success, Runnable fail);

    default void rotate(float yaw, float pitch, boolean desync, Runnable success, Runnable fail) {
        CloseablePositionOperator operator = desync ? PositionUtils.desync() : null;
        if (operator != null) operator.desyncMode(PositionMask.LOOK);
        rotate(yaw, pitch, desync, operator, () -> {
            if (success != null) success.run();
            if (operator != null) operator.close();
        }, () -> {
            if (fail != null) fail.run();
            if (operator != null) operator.close();
        });
    }

    default void rotate(Vec3d lookAt, boolean desync, Runnable success, Runnable fail) {
        float[] vec = BlockUtils.getLookVec(lookAt);
        if (vec == null) return;
        rotate(vec[0], vec[1], desync, success, fail);
    }

    default void rotate(BlockPlaceInfo info, boolean desync, Runnable success, Runnable fail) {
        float[] vec = BlockUtils.getLookVec(info);
        if (vec == null) return;
        rotate(vec[0], vec[1], desync, success, fail);
    }
}
