package net.toshimichi.sushi.gui;

import net.toshimichi.sushi.config.data.Named;

public enum Origin implements Named {
    TOP_LEFT("Top Left", false, false),
    BOTTOM_LEFT("Bottom Right", false, true),
    TOP_RIGHT("Top Right", true, false),
    BOTTOM_RIGHT("Bottom Right", true, true);

    private final String name;
    private final boolean fromRight;
    private final boolean fromBottom;

    Origin(String name, boolean fromRight, boolean fromBottom) {
        this.name = name;
        this.fromRight = fromRight;
        this.fromBottom = fromBottom;
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
}
