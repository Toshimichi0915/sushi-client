package net.sushiclient.client;

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

    public void setName(String name) {
        this.name = name;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }
}
