package net.toshimichi.sushi.modules;

import java.awt.*;

public interface ModuleCategory {

    ModuleCategory COMBAT = new ResourceModuleCategory("Combat", "combat.png");
    ModuleCategory MOVEMENT = new ResourceModuleCategory("Movement", "movement.png");
    ModuleCategory RENDER = new ResourceModuleCategory("Render", "render.png");
    ModuleCategory PLAYER = new ResourceModuleCategory("Player", "player.png");
    ModuleCategory WORLD = new ResourceModuleCategory("World", "world.png");

    static ModuleCategory[] getDefaultCategories() {
        return new ModuleCategory[]{COMBAT, MOVEMENT, RENDER, PLAYER, WORLD};
    }

    String getName();

    Image getIcon();
}
