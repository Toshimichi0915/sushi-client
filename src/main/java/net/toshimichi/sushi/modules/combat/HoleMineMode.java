package net.toshimichi.sushi.modules.combat;

import com.google.gson.annotations.SerializedName;
import net.toshimichi.sushi.config.data.Named;

public enum HoleMineMode implements Named {
    @SerializedName("BEST_EFFORT")
    BEST_EFFORT("Best Effort"),
    @SerializedName("MINE_ONLY")
    MINE_ONLY("Mine Only");

    private final String name;

    HoleMineMode(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
