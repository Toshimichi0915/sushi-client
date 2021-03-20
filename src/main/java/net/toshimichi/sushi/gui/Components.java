package net.toshimichi.sushi.gui;

import java.util.ArrayList;
import java.util.List;

public class Components {

    private static final ArrayList<Component> components = new ArrayList<>();

    public static Component getTopComponent(boolean visibleOnly) {
        if (components.isEmpty()) return null;
        if (visibleOnly) {
            for (Component component : components) {
                if (component.isVisible())
                    return component;
            }
            return null;
        } else {
            return components.get(0);
        }
    }

    public static void setTopComponent(Component component) {
        if (!components.remove(component)) return;
        components.add(0, component);
        components.forEach(c -> c.setFocused(false));
        component.setFocused(true);
    }

    public static Component getTopComponent(int x, int y) {
        for (Component component : components) {
            if (!component.isVisible()) continue;
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
        component.setVisible(true);
        component.onShow();
        components.add(0, component);
    }

    public static void close(Component component) {
        component.setVisible(false);
        component.onClose();
        components.remove(component);
    }

    public static void closeAll() {
        components.clear();
    }

    public static List<Component> getAll() {
        return new ArrayList<>(components);
    }
}
