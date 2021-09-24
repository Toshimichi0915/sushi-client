package net.sushiclient.client.events.player;

import net.sushiclient.client.events.CancellableEvent;
import net.sushiclient.client.events.EventTiming;

public class PlayerTurnEvent extends CancellableEvent {
    private final float yaw;
    private final float pitch;

    public PlayerTurnEvent(EventTiming timing, float yaw, float pitch) {
        super(timing);
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }
}
