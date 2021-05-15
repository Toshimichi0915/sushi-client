package net.toshimichi.sushi.utils;

import java.util.function.Function;

public enum InventoryType {
    HOTBAR(0, 9, i -> i + 36, i -> i - 36),
    MAIN(9, 36, i -> i, i -> i),
    ARMOR(36, 40, i -> i - 31, i -> i + 31),
    OFFHAND(40, 41, i -> i + 5, i -> i - 5);

    private final int min;
    private final int max;
    private final Function<Integer, Integer> toProtocol;
    private final Function<Integer, Integer> fromProtocol;

    InventoryType(int min, int max, Function<Integer, Integer> toProtocol, Function<Integer, Integer> fromProtocol) {
        this.min = min;
        this.max = max;
        this.toProtocol = toProtocol;
        this.fromProtocol = fromProtocol;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public int toProtocol(int slot) {
        return toProtocol.apply(slot);
    }

    public int fromProtocol(int slot) {
        return fromProtocol.apply(slot);
    }

    public static InventoryType valueOf(int slot) {
        for (InventoryType type : values()) {
            if (type.min <= slot && slot < type.max) return type;
        }
        return null;
    }
}
