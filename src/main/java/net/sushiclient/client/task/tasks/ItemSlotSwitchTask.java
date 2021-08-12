package net.sushiclient.client.task.tasks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.sushiclient.client.task.TaskAdapter;
import net.sushiclient.client.utils.player.InventoryUtils;
import net.sushiclient.client.utils.player.ItemSlot;

public class ItemSlotSwitchTask extends TaskAdapter<ItemSlot, Object> {

    @Override
    public void tick() throws Exception {
        stop(null);
        if (getInput() == null) return;
        ItemSlot itemSlot = getInput();
        EntityPlayerSP player = Minecraft.getMinecraft().player;

        if (itemSlot.getIndex() == player.inventory.currentItem) return;

        // hotbar
        if (itemSlot.getIndex() < 9) {
            InventoryUtils.moveHotbar(itemSlot.getIndex());
        } else {
            InventoryUtils.moveToHotbar(itemSlot);
        }
    }
}
