package net.toshimichi.sushi.gui;

public enum Origin {
    TOP_LEFT(false, false),
    BOTTOM_LEFT(false, true),
    TOP_RIGHT(true, false),
    BOTTOM_RIGHT(true, true);

    private final boolean fromRight;
    private final boolean fromBottom;

    Origin(boolean fromRight, boolean fromBottom) {
        this.fromRight = fromRight;
        this.fromBottom = fromBottom;
    }

    public boolean isFromRight() {
        return fromRight;
    }

    public boolean isFromBottom() {
        return fromBottom;
    }
}
