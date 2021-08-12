package net.sushiclient.client.utils.player;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
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
import java.util.function.Predicate;

public class InventoryUtils {

    private static volatile boolean switching;
    private static volatile int hotbarSlot;

    public static boolean isSwitching() {
        return switching;
    }

    public static int getHotbarSlot() {
        return hotbarSlot;
    }

    public static void moveHotbar(int slot) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        player.inventory.currentItem = slot;
        Minecraft.getMinecraft().playerController.updateController();
    }

    public static ItemSlot findAnyTool(EntityPlayerSP player) {
        for (ItemSlot itemSlot : InventoryType.HOTBAR) {
            if (itemSlot.getItemStack().getItem() instanceof ItemTool) return itemSlot;
        }
        return null;
    }

    public static void antiWeakness(boolean b, Runnable r) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (!b || player == null) {
            r.run();
            return;
        }
        ItemSlot swordSlot = InventoryUtils.findAnyTool(player);
        if (swordSlot != null && player.getActivePotionEffect(MobEffects.WEAKNESS) != null) {
            InventoryUtils.silentSwitch(true, swordSlot.getIndex(), r);
        } else {
            r.run();
        }
    }

    public static synchronized void silentSwitch(boolean b, int slot, Runnable r) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        PlayerControllerMP controller = Minecraft.getMinecraft().playerController;
        NetHandlerPlayClient connection = Minecraft.getMinecraft().getConnection();
        if (slot < 0 || slot >= 9) {
            r.run();
            return;
        }
        if (!b || player == null || connection == null) {
            if (Minecraft.getMinecraft().isCallingFromMinecraftThread()) {
                InventoryUtils.moveHotbar(slot);
            }
            r.run();
            return;
        }
        hotbarSlot = slot;
        switching = true;
        int current = ItemSlot.current().getIndex();
        InventoryUtils.moveHotbar(slot);
        r.run();
        InventoryUtils.moveHotbar(current);
        switching = false;
    }

    public static ItemSlot[] find(Predicate<ItemSlot> predicate, Comparator<ItemSlot> comparator, InventoryType... allowed) {
        ArrayList<ItemSlot> list = new ArrayList<>();
        for (InventoryType type : allowed) {
            for (int i = 0; i < type.getSize(); i++) {
                int index = i + type.getIndex();
                if (predicate.test(new ItemSlot(i + type.getIndex())))
                    list.add(new ItemSlot(index, Minecraft.getMinecraft().player));
            }
        }
        list.sort(comparator);
        return list.toArray(new ItemSlot[0]);
    }

    public static ItemSlot[] findItemSlots(Item searching, Comparator<ItemSlot> comparator, InventoryType... allowed) {
        return find(it -> it.getItemStack().getItem() == searching, comparator, allowed);
    }

    public static ItemSlot findItemSlot(Item searching, Comparator<ItemSlot> comparator, InventoryType... allowed) {
        ItemSlot[] array = findItemSlots(searching, comparator, allowed);
        if (array.length == 0) return null;
        else return array[0];
    }

    public static ItemSlot findItemSlot(Item searching, InventoryType... allowed) {
        return findItemSlot(searching, null, allowed);
    }

    public static short clickItemSlot(ItemSlot slot, ClickType type, int button) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        NetHandlerPlayClient connection = player.connection;
        Container container = player.inventoryContainer;
        short transactionId = container.getNextTransactionID(player.inventory);
        ItemStack itemStack = container.slotClick(slot.getProtocolIndex(), button, type, player);
        connection.sendPacket(new CPacketClickWindow(0, slot.getProtocolIndex(), button, type, itemStack, transactionId));
        Minecraft.getMinecraft().playerController.updateController();
        return transactionId;
    }

    public static ItemSlot moveToHotbar(ItemSlot itemSlot) {
        if (itemSlot.getInventoryType() == InventoryType.HOTBAR) return itemSlot;
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        ItemSlot emptyHotbar = InventoryUtils.findItemSlot(null, InventoryType.HOTBAR);
        if (emptyHotbar == null) {
            InventoryUtils.clickItemSlot(itemSlot, ClickType.SWAP, player.inventory.currentItem);
            return new ItemSlot(player.inventory.currentItem);
        } else {
            InventoryUtils.clickItemSlot(itemSlot, ClickType.QUICK_MOVE, 0);
            InventoryUtils.moveHotbar(emptyHotbar.getIndex());
            return new ItemSlot(emptyHotbar.getIndex());
        }
    }

    public static void moveTo(ItemSlot from, ItemSlot to) {
        InventoryUtils.clickItemSlot(from, ClickType.PICKUP, 0);
        InventoryUtils.clickItemSlot(to, ClickType.PICKUP, 0);
        InventoryUtils.clickItemSlot(from, ClickType.PICKUP, 0);
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
            if (!(ItemSlot.current().getItemStack().getItem() instanceof ItemTool)) return ItemSlot.current();
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
