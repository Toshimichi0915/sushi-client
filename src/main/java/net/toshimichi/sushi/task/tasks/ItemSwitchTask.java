package net.toshimichi.sushi.task.tasks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.toshimichi.sushi.task.TaskAdapter;
import net.toshimichi.sushi.utils.InventoryUtils;
import net.toshimichi.sushi.utils.ItemSlot;

import java.util.ArrayList;
import java.util.Comparator;

public class ItemSwitchTask extends TaskAdapter<Item, Boolean> {

    private final Comparator<ItemSlot> comparator;
    private final boolean fromInventory;

    public ItemSwitchTask(Comparator<ItemSlot> comparator, boolean fromInventory) {
        this.comparator = comparator == null ? Comparator.comparingInt(ItemSlot::getIndex) : comparator;
        this.fromInventory = fromInventory;
    }

    private ItemSlot findItemSlot(EntityPlayerSP player) {
        ArrayList<ItemSlot> list = new ArrayList<>();
        for (int i = 0; i < 36; i++) {
            ItemStack itemStack = player.inventory.getStackInSlot(i);
            if (!itemStack.getItem().equals(getInput())) continue;
            list.add(new ItemSlot(i, player));
        }
        list.sort(comparator);
        if (list.isEmpty()) return null;
        else return list.get(0);
    }

    private int getEmptyHotbar(EntityPlayerSP playerSP) {
        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = playerSP.inventory.getStackInSlot(i);
            if (itemStack.isEmpty()) return i;
        }
        return -1;
    }

    @Override
    public void tick() throws Exception {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null) {
            stop(false);
            return;
        }

        ItemSlot itemSlot = findItemSlot(player);
        if (itemSlot == null) {
            stop(false);
            return;
        }

        // hotbar
        if (itemSlot.getIndex() < 9) {
            InventoryUtils.moveHotbar(itemSlot.getIndex());
            stop(true);
            return;
        }

        if (fromInventory) {
            int empty = getEmptyHotbar(player);
            if (empty == -1) {
                InventoryUtils.clickItemSlot(itemSlot, ClickType.SWAP, player.inventory.currentItem);
            } else {
                InventoryUtils.clickItemSlot(itemSlot, ClickType.QUICK_MOVE, 0);
                InventoryUtils.moveHotbar(empty);
            }
            stop(true);
            return;
        }

        stop(false);
    }
}
