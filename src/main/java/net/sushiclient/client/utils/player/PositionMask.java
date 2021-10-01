package net.sushiclient.client.utils.player;

import java.util.Objects;

public class PositionMask {
    public static PositionMask ALL = new PositionMask(true, true, true);
    public static PositionMask POSITION = new PositionMask(true, false, false);
    public static PositionMask LOOK = new PositionMask(false, true, false);
    public static PositionMask POSITION_LOOK = new PositionMask(true, true, false);
    public static PositionMask ON_GROUND = new PositionMask(false, false, true);
    public static PositionMask NONE = new PositionMask(false, false, false);

    private final boolean position;
    private final boolean rotation;
    private final boolean onGround;

    PositionMask(boolean position, boolean rotation, boolean onGround) {
        this.position = position;
        this.rotation = rotation;
        this.onGround = onGround;
    }

    public boolean isPositionDesync() {
        return position;
    }

    public boolean isRotationDesync() {
        return rotation;
    }

    public boolean isOnGroundDesync() {
        return onGround;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PositionMask that = (PositionMask) o;
        return position == that.position && rotation == that.rotation && onGround == that.onGround;
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, rotation, onGround);
    }
}
