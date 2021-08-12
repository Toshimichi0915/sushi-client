package net.sushiclient.client.gui.base;

import net.sushiclient.client.gui.Anchor;
import net.sushiclient.client.gui.Component;
import net.sushiclient.client.gui.ListComponent;
import net.sushiclient.client.gui.Origin;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public class BaseListComponent<T> extends BaseComponent implements ListComponent<T> {

    private final ArrayList<T> internal;

    public BaseListComponent(ArrayList<T> internal) {
        this.internal = internal;
    }

    public BaseListComponent(int x, int y, int width, int height, Anchor anchor, Origin origin, Component parent, ArrayList<T> internal) {
        super(x, y, width, height, anchor, origin, parent);
        this.internal = internal;
    }

    @Override
    public boolean remove(Object o) {
        return internal.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return internal.contains(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return internal.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        return internal.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return internal.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return internal.retainAll(c);
    }

    @Override
    public void replaceAll(UnaryOperator<T> operator) {
        internal.replaceAll(operator);
    }

    @Override
    public void sort(Comparator<? super T> c) {
        internal.sort(c);
    }

    @Override
    public void clear() {
        internal.clear();
    }

    @Override
    public T get(int index) {
        return internal.get(index);
    }

    @Override
    public T set(int index, T element) {
        return internal.set(index, element);
    }

    @Override
    public void add(int index, T element) {
        internal.add(index, element);
    }

    @Override
    public T remove(int index) {
        return internal.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return internal.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return internal.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return internal.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return internal.listIterator(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return internal.subList(fromIndex, toIndex);
    }

    @Override
    public int size() {
        return internal.size();
    }

    @Override
    public boolean isEmpty() {
        return internal.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return internal.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return internal.iterator();
    }

    @Override
    public Object[] toArray() {
        return internal.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return internal.toArray(a);
    }

    @Override
    public boolean add(T t) {
        return internal.add(t);
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        internal.forEach(action);
    }

    @Override
    public Spliterator<T> spliterator() {
        return internal.spliterator();
    }
}
