package net.sushiclient.client.utils.player;

import java.util.Objects;

public class DesyncMode {
    public static DesyncMode ALL = new DesyncMode(true, true, true);
    public static DesyncMode POSITION = new DesyncMode(true, false, false);
    public static DesyncMode LOOK = new DesyncMode(false, true, false);
    public static DesyncMode POSITION_LOOK = new DesyncMode(true, true, false);
    public static DesyncMode ON_GROUND = new DesyncMode(false, false, true);
    public static DesyncMode NONE = new DesyncMode(false, false, false);

    private final boolean position;
    private final boolean rotation;
    private final boolean onGround;

    DesyncMode(boolean position, boolean rotation, boolean onGround) {
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
        DesyncMode that = (DesyncMode) o;
        return position == that.position && rotation == that.rotation && onGround == that.onGround;
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, rotation, onGround);
    }
}
