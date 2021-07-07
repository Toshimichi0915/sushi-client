package net.toshimichi.sushi.gui.hud;

public interface ElementFactory {
    ElementConstructor getElementConstructor();

    String getId();

    String getName();
}
