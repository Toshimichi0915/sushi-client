package net.toshimichi.sushi.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketClickWindow;

import java.util.ArrayList;
import java.util.Comparator;

public class InventoryUtils {

    public static void moveHotbar(int slot) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        PlayerControllerMP controller = Minecraft.getMinecraft().playerController;
        player.inventory.currentItem = slot;
        controller.updateController();
    }

    public static ItemSlot findItemSlot(Item searching, EntityPlayerSP player, Comparator<ItemSlot> comparator, InventoryType... allowed) {
        ArrayList<ItemSlot> list = new ArrayList<>();
        for (InventoryType type : allowed) {
            for (int i = 0; i < type.getSize(); i++) {
                int index = i + type.getIndex();
                ItemStack itemStack = player.inventory.getStackInSlot(index);
                System.out.println(searching);
                if (searching == null && itemStack.isEmpty() || itemStack.getItem().equals(searching))
                    list.add(new ItemSlot(index, player));
            }
        }
        list.sort(comparator);
        if (list.isEmpty()) return null;
        else return list.get(0);
    }

    public static ItemSlot findItemSlot(Item searching, EntityPlayerSP player, InventoryType... allowed) {
        return findItemSlot(searching, player, Comparator.comparingInt(ItemSlot::getIndex), allowed);
    }

    public static short clickItemSlot(ItemSlot slot, ClickType type, int button) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        NetHandlerPlayClient connection = player.connection;
        Container container = player.inventoryContainer;
        short transactionId = container.getNextTransactionID(player.inventory);
        int index = slot.getInventoryType().toProtocol(slot.getIndex());
        container.slotClick(index, button, type, player);
        connection.sendPacket(new CPacketClickWindow(0, index, button, type, slot.getItemStack(), transactionId));
        Minecraft.getMinecraft().playerController.updateController();
        return transactionId;
    }
}
