package net.toshimichi.sushi.gui;

import java.util.ArrayList;
import java.util.List;

public class Components {

    private static final ArrayList<ComponentContext<?>> components = new ArrayList<>();

    public static ComponentContext<?> getTopContext() {
        if (components.isEmpty()) return null;
        return components.get(0);
    }

    public static void setTopComponent(ComponentContext<?> component) {
        if (!components.remove(component)) return;
        components.add(0, component);
        components.forEach(c -> c.getOrigin().setFocused(false));
        component.getOrigin().setFocused(true);
    }

    public static ComponentContext<?> getTopContext(int x, int y) {
        for (ComponentContext<?> context : components) {
            Component component = context.getOrigin();
            int minX = component.getWindowX();
            int minY = component.getWindowY();
            int maxX = minX + component.getWidth();
            int maxY = minY + component.getHeight();
            if (minX <= x && x <= maxX && minY <= y && y <= maxY)
                return context;
        }
        return null;
    }

    public static <T extends Component> ComponentContext<T> show(T component, boolean close, int index) {
        if (close) closeAll();
        BaseComponentContext<T> context = new BaseComponentContext<>(component);
        components.add(index, context);
        component.setContext(context);
        component.onShow();
        return context;
    }

    public static <T extends Component> ComponentContext<T> show(T component, boolean close) {
        return show(component, close, 0);
    }

    private static void close(ComponentContext<?> component) {
        if (!component.getOrigin().isClosed()) {
            component.getOrigin().onClose();
        }
        components.remove(component);
    }

    public static void closeAll() {
        new ArrayList<>(components).forEach(Components::close);
    }

    public static List<ComponentContext<?>> getAll() {
        return new ArrayList<>(components);
    }

    private static class BaseComponentContext<T extends Component> implements ComponentContext<T> {
        final T origin;

        public BaseComponentContext(T origin) {
            this.origin = origin;
        }

        @Override
        public T getOrigin() {
            return origin;
        }

        @Override
        public void close() {
            Components.close(this);
        }
    }
}
