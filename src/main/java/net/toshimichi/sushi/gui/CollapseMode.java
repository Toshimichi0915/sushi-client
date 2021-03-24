package net.toshimichi.sushi.gui;

import net.toshimichi.sushi.config.data.Named;

public enum CollapseMode implements Named {
    UP("Up"), DOWN("Down");

    private final String name;

    CollapseMode(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
