package net.toshimichi.sushi.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.network.play.client.CPacketClickWindow;

public class InventoryUtils {

    public static void moveHotbar(int slot) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        PlayerControllerMP controller = Minecraft.getMinecraft().playerController;
        player.inventory.currentItem = slot;
        controller.updateController();
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
