package net.toshimichi.sushi;

import com.google.gson.annotations.SerializedName;

class ModConfig {
    @SerializedName("name")
    private String name = "default";
    @SerializedName("theme")
    private String theme = "simple";

    public String getName() {
        return name;
    }

    public String getTheme() {
        return theme;
    }
}
