package net.toshimichi.sushi.modules.combat;

import net.toshimichi.sushi.config.data.Named;

public enum HoleMineMode implements Named {
    BEST_EFFORT("Best Effort"), MINE_ONLY("Mine Only");

    private final String name;

    HoleMineMode(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
