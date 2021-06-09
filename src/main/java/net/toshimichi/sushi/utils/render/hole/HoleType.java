package net.toshimichi.sushi.utils.render.hole;

public enum HoleType {
    SAFE(true, false),
    UNSAFE(false, false),
    SAFE_DOUBLE(true, true),
    UNSAFE_DOUBLE(false, true);

    private final boolean isSafe;
    private final boolean isDouble;

    HoleType(boolean isSafe, boolean isDouble) {
        this.isSafe = isSafe;
        this.isDouble = isDouble;
    }

    public boolean isSafe() {
        return isSafe;
    }

    public boolean isDouble() {
        return isDouble;
    }
}
