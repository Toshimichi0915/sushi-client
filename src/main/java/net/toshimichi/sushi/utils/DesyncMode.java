package net.toshimichi.sushi.utils;

public enum DesyncMode {
    BOTH(true, true),
    POSITION(true, false),
    LOOK(false, true),
    NONE(false, false);

    private final boolean position;
    private final boolean rotation;

    DesyncMode(boolean position, boolean rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    public boolean isPositionDesync() {
        return position;
    }

    public boolean isRotationDesync() {
        return rotation;
    }

    public static DesyncMode valueOf(boolean position, boolean rotation) {
        for (DesyncMode mode : values()) {
            if (mode.isPositionDesync() == position &&
                    mode.isRotationDesync() == rotation) {
                return mode;
            }
        }
        throw new IllegalStateException();
    }
}
