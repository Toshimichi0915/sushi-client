package net.sushiclient.client.modules;

import com.google.gson.annotations.SerializedName;
import net.sushiclient.client.config.data.Named;

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
