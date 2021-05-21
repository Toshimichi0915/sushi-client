package net.toshimichi.sushi.utils;

public enum DesyncMode {
    ALL(true, true, true),
    POSITION(true, false, false),
    LOOK(false, true, false),
    ON_GROUND(false, false, true),
    NONE(false, false, false);

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

    public static DesyncMode valueOf(boolean position, boolean rotation, boolean onGround) {
        for (DesyncMode mode : values()) {
            if (mode.isPositionDesync() == position &&
                    mode.isRotationDesync() == rotation &&
                    mode.isOnGroundDesync() == onGround) {
                return mode;
            }
        }
        throw new IllegalStateException();
    }
}
