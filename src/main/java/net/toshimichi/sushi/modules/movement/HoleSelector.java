package net.toshimichi.sushi.modules.movement;

import com.google.gson.annotations.SerializedName;
import net.toshimichi.sushi.config.data.Named;
import net.toshimichi.sushi.utils.render.hole.HoleType;

public enum HoleSelector implements Named {
    @SerializedName("BOTH")
    BOTH("Both", HoleType.SAFE, HoleType.UNSAFE),
    @SerializedName("BEDROCK")
    BEDROCK("Bedrock", HoleType.SAFE);

    private final String name;
    private final HoleType[] allowed;

    HoleSelector(String name, HoleType... allowed) {
        this.name = name;
        this.allowed = allowed;
    }

    @Override
    public String getName() {
        return name;
    }

    public HoleType[] getAllowedTypes() {
        return allowed;
    }
}
