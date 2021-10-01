package net.sushiclient.client.utils.player;

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
}
