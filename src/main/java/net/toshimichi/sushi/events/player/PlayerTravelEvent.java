package net.toshimichi.sushi.events.player;

import net.toshimichi.sushi.events.CancellableEvent;
import net.toshimichi.sushi.events.EventTiming;

public class PlayerTravelEvent extends CancellableEvent {

    private float strafe;
    private float vertical;
    private float forward;

    public PlayerTravelEvent(EventTiming timing, float strafe, float vertical, float forward) {
        super(timing);
        this.strafe = strafe;
        this.vertical = vertical;
        this.forward = forward;
    }

    public float getStrafe() {
        return strafe;
    }

    public void setStrafe(float strafe) {
        this.strafe = strafe;
    }

    public float getVertical() {
        return vertical;
    }

    public void setVertical(float vertical) {
        this.vertical = vertical;
    }

    public float getForward() {
        return forward;
    }

    public void setForward(float forward) {
        this.forward = forward;
    }
}
