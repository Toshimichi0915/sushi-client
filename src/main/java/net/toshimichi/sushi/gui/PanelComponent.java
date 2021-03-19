package net.toshimichi.sushi.gui;

import net.toshimichi.sushi.events.input.ClickType;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class PanelComponent extends BaseComponent implements List<Component> {

    private final ArrayList<Component> children = new ArrayList<>();
    private final Function<Component, Component> frameFunction;

    public PanelComponent(Function<Component, Component> frameFunction) {
        this.frameFunction = frameFunction;
    }

    public PanelComponent(int x, int y, int width, int height, Anchor anchor, Component origin, Function<Component, Component> frameFunction) {
        super(x, y, width, height, anchor, origin);
        this.frameFunction = frameFunction;
    }

    private Component getFocusedComponent() {
        for (Component component : children) {
            if (component.isFocused()) return component;
        }
        return null;
    }

    private void execFocus(Consumer<Component> consumer) {
        Component focused = getFocusedComponent();
        if (focused != null) {
            consumer.accept(focused);
        }
    }

    private void setFocusedComponent(Component component) {
        children.forEach(c -> c.setFocused(false));
        component.setFocused(true);
    }

    private Component getTopComponent(int x, int y) {
        for (Component child : children) {
            if(!child.isVisible()) continue;
            if (child.getWindowX() > x) continue;
            if (child.getWindowX() + child.getWidth() > x) continue;
            if (child.getWindowY() > y) continue;
            if (child.getWindowY() + child.getHeight() > y) continue;
            return child;
        }
        return null;
    }

    @Override
    public void onRender() {
        ArrayList<Component> clone = new ArrayList<>(children);
        Collections.reverse(clone);
        for (Component component : clone) {
            component.onRender();
        }
    }

    @Override
    public void onClick(int x, int y, ClickType type) {
        Component topComponent = getTopComponent(x, y);
        if (topComponent == null) return;
        setFocusedComponent(topComponent);
        topComponent.onClick(x, y, type);
    }

    @Override
    public void onHold(int fromX, int fromY, int toX, int toY, ClickType type, MouseStatus status) {
        Component from = getTopComponent(fromX, fromY);
        Component to = getTopComponent(toX, toY);
        if (from == null) return;
        if (!from.equals(to) && status != MouseStatus.START) {
            from.onHold(fromX, fromY, toX, toY, type, MouseStatus.CANCEL);
        }
        if (to == null) return;
        to.onHold(fromX, fromY, toX, toY, type, status);
    }

    @Override
    public void onScroll(int deltaX, int deltaY, ClickType type) {
        execFocus(c -> c.onScroll(deltaX, deltaY, type));
    }

    @Override
    public void onKeyPressed(int keyCode) {
        execFocus(c -> c.onKeyPressed(keyCode));
    }

    @Override
    public void onKeyReleased(int keyCode) {
        super.onKeyReleased(keyCode);
        execFocus(c -> c.onKeyPressed(keyCode));
    }

    @Override
    public boolean add(Component component) {
        component = frameFunction.apply(component);
        int windowX = component.getWindowX();
        int windowY = component.getWindowY();
        component.setOrigin(this);
        component.setWindowX(windowX);
        component.setWindowY(windowY);
        return children.add(component);
    }

    @Override
    public boolean remove(Object o) {
        return children.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return children.contains(c);
    }

    @Override
    public boolean addAll(Collection<? extends Component> c) {
        return children.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends Component> c) {
        return children.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return children.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return children.retainAll(c);
    }

    @Override
    public void clear() {
        children.clear();
    }

    @Override
    public Component get(int index) {
        return children.get(index);
    }

    @Override
    public Component set(int index, Component element) {
        return children.set(index, element);
    }

    @Override
    public void add(int index, Component element) {
        children.add(index, element);
    }

    @Override
    public Component remove(int index) {
        return children.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return children.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return children.lastIndexOf(o);
    }

    @Override
    public ListIterator<Component> listIterator() {
        return children.listIterator();
    }

    @Override
    public ListIterator<Component> listIterator(int index) {
        return children.listIterator(index);
    }

    @Override
    public List<Component> subList(int fromIndex, int toIndex) {
        return children.subList(fromIndex, toIndex);
    }

    @Override
    public int size() {
        return children.size();
    }

    @Override
    public boolean isEmpty() {
        return children.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return children.contains(o);
    }

    @Override
    public Iterator<Component> iterator() {
        return children.iterator();
    }

    @Override
    public Object[] toArray() {
        return children.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return children.toArray(a);
    }

    @Override
    public void forEach(Consumer<? super Component> action) {
        children.forEach(action);
    }

    @Override
    public Spliterator<Component> spliterator() {
        return children.spliterator();
    }
}
