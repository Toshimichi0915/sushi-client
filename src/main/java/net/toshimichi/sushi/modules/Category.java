package net.toshimichi.sushi.modules;

import java.awt.*;

public interface Category {

    Category COMBAT = new ResourceImageCategory("Combat", "combat.png");
    Category MOVEMENT = new ResourceImageCategory("Movement", "movement.png");
    Category RENDER = new ResourceImageCategory("Render", "render.png");
    Category PLAYER = new ResourceImageCategory("Player", "player.png");
    Category WORLD = new ResourceImageCategory("World", "world.png");
    Category CLIENT = new ResourceImageCategory("Client", "client.png");

    static Category[] getDefaultCategories() {
        return new Category[]{COMBAT, MOVEMENT, RENDER, PLAYER, WORLD};
    }

    String getName();

    Image getIcon();
}
