package net.sushiclient.client.gui;

import com.google.gson.annotations.SerializedName;
import net.sushiclient.client.config.data.Named;

public enum CollapseMode implements Named {
    @SerializedName("UP")
    UP("Up"),
    @SerializedName("DOWN")
    DOWN("Down");

    private final String name;

    CollapseMode(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
