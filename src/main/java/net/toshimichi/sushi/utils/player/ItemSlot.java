package net.toshimichi.sushi.utils.player;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemStack;

public class ItemSlot implements Comparable<ItemSlot> {
    private final int index;
    private final InventoryType inventoryType;
    private final EntityPlayerSP player;

    public ItemSlot(int index, EntityPlayerSP player) {
        this.index = index;
        this.inventoryType = InventoryType.valueOf(index);
        this.player = player;
    }

    public ItemSlot(int index) {
        this(index, Minecraft.getMinecraft().player);
    }

    public int getIndex() {
        return index;
    }

    public ItemStack getItemStack() {
        return player.inventory.getStackInSlot(index);
    }

    public InventoryType getInventoryType() {
        return inventoryType;
    }

    @Override
    public int compareTo(ItemSlot o) {
        return Integer.compare(getIndex(), o.getIndex());
    }
}
