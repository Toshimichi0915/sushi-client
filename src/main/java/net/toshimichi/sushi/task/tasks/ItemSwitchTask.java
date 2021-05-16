package net.toshimichi.sushi.task.tasks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.toshimichi.sushi.task.TaskAdapter;
import net.toshimichi.sushi.utils.InventoryType;
import net.toshimichi.sushi.utils.InventoryUtils;
import net.toshimichi.sushi.utils.ItemSlot;

import java.util.Comparator;

public class ItemSwitchTask extends TaskAdapter<Item, Boolean> {

    private final Comparator<ItemSlot> comparator;
    private final boolean fromInventory;

    public ItemSwitchTask(Comparator<ItemSlot> comparator, boolean fromInventory) {
        this.comparator = comparator;
        this.fromInventory = fromInventory;
    }

    @Override
    public void tick() throws Exception {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null) {
            stop(false);
            return;
        }

        ItemSlot itemSlot = InventoryUtils.findItemSlot(getInput(), player, comparator, InventoryType.values());
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
            ItemSlot emptyHotbar = InventoryUtils.findItemSlot(null, player, InventoryType.HOTBAR);
            if (emptyHotbar == null) {
                InventoryUtils.clickItemSlot(itemSlot, ClickType.SWAP, player.inventory.currentItem);
            } else {
                InventoryUtils.clickItemSlot(itemSlot, ClickType.QUICK_MOVE, 0);
                InventoryUtils.moveHotbar(emptyHotbar.getIndex());
            }
            stop(true);
            return;
        }

        stop(false);
    }
}