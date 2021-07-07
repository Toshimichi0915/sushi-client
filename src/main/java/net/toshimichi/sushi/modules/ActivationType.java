package net.toshimichi.sushi.modules;

import com.google.gson.annotations.SerializedName;
import net.toshimichi.sushi.config.data.Named;

public enum ActivationType implements Named {
    @SerializedName("Toggle")
    TOGGLE("Toggle"),
    @SerializedName("Hold")
    HOLD("Hold");

    private final String name;

    ActivationType(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
