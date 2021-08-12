package net.sushiclient.client.task.tasks;

import com.google.gson.annotations.SerializedName;
import net.sushiclient.client.config.data.Named;

public enum ItemSwitchMode implements Named {
    @SerializedName("ALL")
    INVENTORY("All"),
    @SerializedName("HOTBAR")
    HOTBAR("Hotbar"),
    @SerializedName("NONE")
    NONE("None");

    private final String name;

    ItemSwitchMode(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
