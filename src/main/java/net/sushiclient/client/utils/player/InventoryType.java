package net.sushiclient.client.utils.player;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public enum InventoryType implements Iterable<ItemSlot> {
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

    private List<ItemSlot> getItemSlotList() {
        ArrayList<ItemSlot> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            result.add(new ItemSlot(i + min));
        }
        return result;
    }

    public ItemSlot findStackable(ItemStack itemStack) {
        itemStack = itemStack.copy();
        itemStack.setCount(1);
        for (ItemSlot candidate : this) {
            ItemStack stack = candidate.getItemStack().copy();
            stack.setCount(1);
            if (ItemStack.areItemStacksEqual(itemStack, stack)) return candidate;
        }
        return null;
    }

    @Override
    public Iterator<ItemSlot> iterator() {
        return getItemSlotList().iterator();
    }

    public ItemSlot[] getAll() {
        return getItemSlotList().toArray(new ItemSlot[0]);
    }
}
