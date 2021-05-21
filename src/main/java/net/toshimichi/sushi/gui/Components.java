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

    public static <T extends Component> ComponentContext<T> show(T component, boolean overlay, boolean close, int index) {
        if (close) closeAll();
        BaseComponentContext<T> context = new BaseComponentContext<>(component, overlay);
        components.add(index, context);
        component.setContext(context);
        component.setVisible(true);
        return context;
    }

    public static <T extends Component> ComponentContext<T> show(T component, boolean overlay, boolean close) {
        return show(component, overlay, close, 0);
    }

    private static void close(ComponentContext<?> component) {
        component.getOrigin().setVisible(false);
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
        final boolean overlay;

        public BaseComponentContext(T origin, boolean overlay) {
            this.origin = origin;
            this.overlay = overlay;
        }

        @Override
        public T getOrigin() {
            return origin;
        }

        @Override
        public boolean isOverlay() {
            return overlay;
        }

        @Override
        public void close() {
            Components.close(this);
        }
    }
}
