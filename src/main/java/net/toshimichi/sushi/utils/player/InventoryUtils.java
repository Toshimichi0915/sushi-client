package net.toshimichi.sushi.utils.player;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.init.Enchantments;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.play.client.CPacketClickWindow;

import java.util.ArrayList;
import java.util.Comparator;

public class InventoryUtils {

    public static void moveHotbar(int slot) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        player.inventory.currentItem = slot;
        Minecraft.getMinecraft().playerController.updateController();
    }

    public static ItemSlot findAnyTool(EntityPlayerSP player) {
        for(ItemSlot itemSlot : InventoryType.HOTBAR) {
            if(itemSlot.getItemStack().getItem() instanceof ItemTool) return itemSlot;
        }
        return null;
    }

    public static void antiWeakness(boolean b, Runnable r) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if(!b || player == null) {
            r.run();
            return;
        }
        ItemSlot currentSlot = ItemSlot.current();
        ItemSlot swordSlot = InventoryUtils.findAnyTool(player);
        boolean switchBack = false;
        if(swordSlot != null && player.getActivePotionEffect(MobEffects.WEAKNESS) != null) {
            InventoryUtils.moveHotbar(swordSlot.getIndex());
            switchBack = true;
        }
        r.run();
        if (switchBack) {
            InventoryUtils.moveHotbar(currentSlot.getIndex());
        }
    }

    public static ItemSlot findItemSlot(Item searching, EntityPlayerSP player, Comparator<ItemSlot> comparator, InventoryType... allowed) {
        ArrayList<ItemSlot> list = new ArrayList<>();
        for (InventoryType type : allowed) {
            for (int i = 0; i < type.getSize(); i++) {
                int index = i + type.getIndex();
                ItemStack itemStack = player.inventory.getStackInSlot(index);
                if (searching == null && itemStack.isEmpty() || itemStack.getItem().equals(searching))
                    list.add(new ItemSlot(index, player));
            }
        }
        list.sort(comparator);
        if (list.isEmpty()) return null;
        else return list.get(0);
    }

    public static ItemSlot findItemSlot(Item searching, EntityPlayerSP player, InventoryType... allowed) {
        return findItemSlot(searching, player, null, allowed);
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

    public static void moveToHotbar(ItemSlot itemSlot) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        ItemSlot emptyHotbar = InventoryUtils.findItemSlot(null, player, InventoryType.HOTBAR);
        if (emptyHotbar == null) {
            InventoryUtils.clickItemSlot(itemSlot, ClickType.SWAP, player.inventory.currentItem);
        } else {
            InventoryUtils.clickItemSlot(itemSlot, ClickType.QUICK_MOVE, 0);
            InventoryUtils.moveHotbar(emptyHotbar.getIndex());
        }
    }

    public static ItemSlot findBestWeapon(boolean fromInventory, boolean preferAxe) {
        int max = fromInventory ? 36 : 9;
        ItemSlot item = null;
        float maxDamage = 0;
        for (int i = 0; i < max; i++) {
            ItemSlot itemSlot = new ItemSlot(i, Minecraft.getMinecraft().player);
            ItemStack itemStack = itemSlot.getItemStack();
            float damage = ItemUtils.getAttackDamage(itemStack);
            if (!preferAxe) {
                if (item != null && item.getItemStack().getItem() instanceof ItemSword) {
                    if (itemStack.getItem() instanceof ItemSword && damage > maxDamage) {
                        item = itemSlot;
                        maxDamage = damage;
                    }
                } else if (damage > maxDamage) {
                    item = itemSlot;
                    maxDamage = damage;
                }
            } else if (damage > maxDamage) {
                item = itemSlot;
                maxDamage = damage;
            }
        }
        return item;
    }

    public static ItemSlot findBestTool(boolean fromInventory, boolean preferSilkTouch, IBlockState blockState) {
        int max = fromInventory ? 36 : 9;
        ItemSlot item = null;
        float fastest = 0;
        for (int i = 0; i < max; i++) {
            ItemSlot itemSlot = new ItemSlot(i, Minecraft.getMinecraft().player);
            ItemStack itemStack = itemSlot.getItemStack();
            float destroySpeed = ItemUtils.getDestroySpeed(blockState, itemStack);
            if (preferSilkTouch) {
                if (item != null && ItemUtils.getEnchantmentLevel(item.getItemStack(), Enchantments.SILK_TOUCH) != 0) {
                    if (ItemUtils.getEnchantmentLevel(itemStack, Enchantments.SILK_TOUCH) != 0 && destroySpeed > fastest) {
                        item = itemSlot;
                        fastest = destroySpeed;
                    }
                } else if (destroySpeed > fastest) {
                    item = itemSlot;
                    fastest = destroySpeed;
                }
            } else if (destroySpeed > fastest) {
                item = itemSlot;
                fastest = destroySpeed;
            }
        }
        if (Double.compare(fastest, 1) == 0) {
            for (int i = 0; i < 9; i++) {
                ItemSlot hotbarItemSlot = new ItemSlot(i);
                Item hotbarItem = hotbarItemSlot.getItemStack().getItem();
                if (!(hotbarItem instanceof ItemTool)) return hotbarItemSlot;
            }
        }
        return item;
    }

    public static boolean hasItem(Item item) {
        for (ItemSlot itemSlot : ItemSlot.values()) {
            if (itemSlot.getItemStack().getItem().equals(item)) return true;
        }
        return false;
    }
}
