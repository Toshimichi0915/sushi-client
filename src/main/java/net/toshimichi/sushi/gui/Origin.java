package net.toshimichi.sushi.gui;

import com.google.gson.annotations.SerializedName;
import net.toshimichi.sushi.config.data.Named;

public enum Origin implements Named {
    @SerializedName("TOP_LEFT")
    TOP_LEFT("Top Left", false, false, Anchor.TOP_LEFT),
    @SerializedName("BOTTOM_LEFT")
    BOTTOM_LEFT("Bottom Right", false, true, Anchor.BOTTOM_LEFT),
    @SerializedName("TOP_RIGHT")
    TOP_RIGHT("Top Right", true, false, Anchor.TOP_RIGHT),
    @SerializedName("BOTTOM_RIGHT")
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

    public Origin getOpposite() {
        for (Origin origin : values()) {
            if (isFromRight() != origin.isFromRight() && isFromBottom() != origin.isFromBottom())
                return origin;
        }
        return null;
    }
}
