package net.toshimichi.sushi.modules;

import net.toshimichi.sushi.config.data.Named;

public enum ActivationType implements Named {
    TOGGLE("Toggle"), HOLD("Hold");

    private final String name;

    ActivationType(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
