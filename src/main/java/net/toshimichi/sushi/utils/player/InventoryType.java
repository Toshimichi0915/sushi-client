package net.toshimichi.sushi.utils.player;

import java.util.function.Function;

public enum InventoryType {
    HOTBAR(0, 9, i -> i + 36, i -> i - 36),
    MAIN(9, 27, i -> i, i -> i),
    ARMOR(36, 4, i -> 44 - i, i -> 44 - i),
    OFFHAND(40, 1, i -> i + 5, i -> i - 5);

    private final int min;
    private final int size;
    private final Function<Integer, Integer> toProtocol;
    private final Function<Integer, Integer> fromProtocol;

    InventoryType(int min, int size, Function<Integer, Integer> toProtocol, Function<Integer, Integer> fromProtocol) {
        this.min = min;
        this.size = size;
        this.toProtocol = toProtocol;
        this.fromProtocol = fromProtocol;
    }

    public int getIndex() {
        return min;
    }

    public int getSize() {
        return size;
    }

    public int toProtocol(int slot) {
        return toProtocol.apply(slot);
    }

    public int fromProtocol(int slot) {
        return fromProtocol.apply(slot);
    }

    public static InventoryType valueOf(int slot) {
        for (InventoryType type : values()) {
            if (type.min <= slot && slot < type.min + type.size) return type;
        }
        return null;
    }
}
