package net.toshimichi.sushi.gui;

import net.toshimichi.sushi.config.data.Named;

public enum Origin implements Named {
    TOP_LEFT("Top Left", false, false, Anchor.TOP_LEFT),
    BOTTOM_LEFT("Bottom Right", false, true, Anchor.BOTTOM_LEFT),
    TOP_RIGHT("Top Right", true, false, Anchor.TOP_RIGHT),
    BOTTOM_RIGHT("Bottom Right", true, true, Anchor.BOTTOM_RIGHT);

    private final String name;
    private final boolean fromRight;
    private final boolean fromBottom;
    private final Anchor anchor;

    Origin(String name, boolean fromRight, boolean fromBottom, Anchor anchor) {
        this.name = name;
        this.fromRight = fromRight;
        this.fromBottom = fromBottom;
        this.anchor = anchor;
    }

    public boolean isFromRight() {
        return fromRight;
    }

    public boolean isFromBottom() {
        return fromBottom;
    }

    @Override
    public String getName() {
        return name;
    }

    public Anchor toAnchor() {
        return anchor;
    }
}
