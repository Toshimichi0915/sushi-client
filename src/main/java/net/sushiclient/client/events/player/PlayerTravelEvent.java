package net.sushiclient.client.events.player;

import net.sushiclient.client.events.CancellableEvent;
import net.sushiclient.client.events.EventTiming;

public class PlayerTravelEvent extends CancellableEvent {

    private final float strafe;
    private final float vertical;
    private final float forward;

    public PlayerTravelEvent(EventTiming timing, float strafe, float vertical, float forward) {
        super(timing);
        this.strafe = strafe;
        this.vertical = vertical;
        this.forward = forward;
    }

    public float getStrafe() {
        return strafe;
    }

    public float getVertical() {
        return vertical;
    }

    public float getForward() {
        return forward;
    }
}
