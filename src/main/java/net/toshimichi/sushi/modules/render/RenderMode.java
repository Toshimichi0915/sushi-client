package net.toshimichi.sushi.modules.render;

import com.google.gson.annotations.SerializedName;
import net.toshimichi.sushi.config.data.Named;

public enum RenderMode implements Named {
    @SerializedName("FULL")
    FULL("Full"),
    @SerializedName("SURFACE")
    SURFACE("Surface");

    private final String name;

    RenderMode(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
