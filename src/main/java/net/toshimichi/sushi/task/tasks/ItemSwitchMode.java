package net.toshimichi.sushi.task.tasks;

import com.google.gson.annotations.SerializedName;
import net.toshimichi.sushi.config.data.Named;

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
