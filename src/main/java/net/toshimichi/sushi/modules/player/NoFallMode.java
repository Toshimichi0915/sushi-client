package net.toshimichi.sushi.modules.player;

import com.google.gson.annotations.SerializedName;
import net.toshimichi.sushi.config.data.Named;

public enum NoFallMode implements Named {
    @SerializedName("PACKET")
    PACKET("Packet"),
    @SerializedName("ON_GROUND")
    ON_GROUND("On Ground"),
    @SerializedName("FLY")
    FLY("Fly");

    private final String name;

    NoFallMode(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
