package net.toshimichi.sushi.modules.render;

import net.toshimichi.sushi.config.data.Named;

public enum RenderMode implements Named {
    FULL("Full"), SURFACE("Surface"), NONE("None");

    private final String name;

    RenderMode(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
