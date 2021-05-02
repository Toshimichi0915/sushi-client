package net.toshimichi.sushi.events.player;

import net.minecraft.entity.MoverType;
import net.toshimichi.sushi.events.CancellableEvent;
import net.toshimichi.sushi.events.EventTiming;

public class PlayerMotionEvent extends CancellableEvent {
    private MoverType type;
    private double x;
    private double y;
    private double z;

    public PlayerMotionEvent(EventTiming timing, MoverType type, double x, double y, double z) {
        super(timing);
        this.type = type;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public MoverType getType() {
        return type;
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

    public void setType(MoverType type) {
        this.type = type;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }
}
