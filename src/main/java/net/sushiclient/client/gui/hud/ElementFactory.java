package net.sushiclient.client.gui.hud;

public interface ElementFactory {
    ElementConstructor getElementConstructor();

    String getId();

    String getName();
}
