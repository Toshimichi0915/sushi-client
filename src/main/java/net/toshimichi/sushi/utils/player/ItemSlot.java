package net.toshimichi.sushi.utils.player;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    public static ItemSlot getCurrentItemSlot(EntityPlayer player) {
        return new ItemSlot(player.inventory.currentItem, player);
    }

    public static ItemSlot[] values() {
        ItemSlot[] result = new ItemSlot[36];
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        for(int i = 0; i < result.length; i++) {
            result[i] = new ItemSlot(i);
        }
        return result;
    }
}
