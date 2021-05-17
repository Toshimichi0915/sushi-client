package net.toshimichi.sushi.utils;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemStack;

import java.util.Comparator;

public class ItemSlot implements Comparable<ItemSlot> {
    private final int index;
    private final ItemStack itemStack;
    private final InventoryType inventoryType;

    public ItemSlot(int index, EntityPlayerSP player) {
        this.index = index;
        this.itemStack = player.inventory.getStackInSlot(index);
        this.inventoryType = InventoryType.valueOf(index);
    }

    public ItemSlot(int index, ItemStack itemStack, InventoryType inventoryType) {
        this.index = index;
        this.itemStack = itemStack;
        this.inventoryType = inventoryType;
    }

    public int getIndex() {
        return index;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public InventoryType getInventoryType() {
        return inventoryType;
    }

    @Override
    public int compareTo(ItemSlot o) {
        return Integer.compare(getIndex(), o.getIndex());
    }
}
