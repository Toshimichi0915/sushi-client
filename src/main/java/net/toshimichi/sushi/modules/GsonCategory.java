package net.toshimichi.sushi.modules;

import java.awt.Image;

public class GsonCategory implements Category {

    private final String name;

    public GsonCategory(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Image getIcon() {
        return null;
    }
}
