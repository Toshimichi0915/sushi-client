package net.toshimichi.sushi.gui;

import java.util.ArrayList;
import java.util.List;

public class Components {

    private static final ArrayList<Component> components = new ArrayList<>();

    public static Component getTopComponent(int x, int y) {
        for (Component component : components) {
            int minX = component.getWindowX();
            int minY = component.getWindowY();
            int maxX = minX + component.getWidth();
            int maxY = minY + component.getHeight();
            if (minX <= x && x <= maxX && minY <= y && y <= maxY)
                return component;
        }
        return null;
    }

    public static void show(Component component, boolean close) {
        if (close)
            components.clear();
        components.add(0, component);
    }

    public static void close() {
        components.clear();
    }

    public static List<Component> getComponents() {
        return new ArrayList<>(components);
    }
}
