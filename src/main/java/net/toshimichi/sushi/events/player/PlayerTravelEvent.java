package net.toshimichi.sushi.events.player;

import net.toshimichi.sushi.events.CancellableEvent;
import net.toshimichi.sushi.events.EventTiming;

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
