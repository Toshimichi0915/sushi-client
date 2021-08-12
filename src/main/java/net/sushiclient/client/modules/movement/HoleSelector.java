package net.sushiclient.client.modules.movement;

import com.google.gson.annotations.SerializedName;
import net.sushiclient.client.config.data.Named;
import net.sushiclient.client.utils.render.hole.HoleType;

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
