package net.sushiclient.client.utils;

import net.sushiclient.client.config.data.Named;

public enum ReachType implements Named {
    VANILLA("Vanilla"), LEGIT("Legit");

    private final String name;

    ReachType(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
