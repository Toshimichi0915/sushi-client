package net.sushiclient.client.utils.player;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Consumer;

public class PositionPacketUtils {

    private static final Map<Object, Consumer<Integer>> weakListeners = new WeakHashMap<>();
    private static int counter;

    public static void increment() {
        weakListeners.values().forEach(it -> it.accept(++counter));
    }

    public static int current() {
        return counter;
    }

    public static void addListener(Object holder, Consumer<Integer> c) {
        weakListeners.put(holder, c);
    }
}
