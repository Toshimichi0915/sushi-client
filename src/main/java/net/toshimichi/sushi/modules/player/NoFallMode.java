package net.toshimichi.sushi.modules.player;

import net.toshimichi.sushi.config.data.Named;

enum NoFallMode implements Named {
    PACKET("Packet"), ON_GROUND("On Ground"), FLY("Fly");

    private final String name;

    NoFallMode(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
