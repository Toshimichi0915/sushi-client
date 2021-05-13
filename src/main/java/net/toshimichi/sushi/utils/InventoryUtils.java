package net.toshimichi.sushi.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class InventoryUtils {
    public static List<Integer> findItemFromHotbar(Item item) {
        ArrayList<Integer> result = new ArrayList<>();
        List<ItemStack> items = Minecraft.getMinecraft().player.inventory.mainInventory;
        for (int i = 0; i < 9; i++) {
            if (items.get(i).getItem().equals(item)) result.add(i);
        }
        return result;
    }
}
