package net.toshimichi.sushi.task.tasks;

import net.toshimichi.sushi.config.data.Named;

public enum ItemSwitchMode implements Named {
    INVENTORY("All"), HOTBAR("Hotbar"), NONE("None");

    private final String name;

    ItemSwitchMode(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
