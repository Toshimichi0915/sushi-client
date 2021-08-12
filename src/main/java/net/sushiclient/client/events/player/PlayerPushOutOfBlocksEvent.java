package net.sushiclient.client.events.player;

import net.sushiclient.client.events.CancellableEvent;

public class PlayerPushOutOfBlocksEvent extends CancellableEvent {
    private final double x;
    private final double y;
    private final double z;

    public PlayerPushOutOfBlocksEvent(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }
}
