package net.toshimichi.sushi.utils.player;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ItemSlot implements Comparable<ItemSlot> {
    private final int index;
    private final InventoryType inventoryType;
    private final EntityPlayer player;

    public ItemSlot(int index, EntityPlayer player) {
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

    public int getProtocolIndex() {
        return inventoryType.toProtocol(index);
    }

    public ItemStack getItemStack() {
        return player.inventory.getStackInSlot(index);
    }

    public InventoryType getInventoryType() {
        return inventoryType;
    }

    public static ItemSlot current() {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        return new ItemSlot(player.inventory.currentItem, player);
    }

    public static ItemSlot offhand() {
        return new ItemSlot(InventoryType.OFFHAND.getIndex());
    }

    public static ItemSlot[] values() {
        ItemSlot[] result = new ItemSlot[36];
        for (int i = 0; i < result.length; i++) {
            result[i] = new ItemSlot(i);
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemSlot itemSlot = (ItemSlot) o;

        if (index != itemSlot.index) return false;
        if (inventoryType != itemSlot.inventoryType) return false;
        return player != null ? player.equals(itemSlot.player) : itemSlot.player == null;
    }

    @Override
    public int hashCode() {
        int result = index;
        result = 31 * result + (inventoryType != null ? inventoryType.hashCode() : 0);
        result = 31 * result + (player != null ? player.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(ItemSlot o) {
        return Integer.compare(getIndex(), o.getIndex());
    }
}
