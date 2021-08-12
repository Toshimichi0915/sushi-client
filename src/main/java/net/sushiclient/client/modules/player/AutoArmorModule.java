package net.sushiclient.client.modules.player;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.EntityLiving;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.tick.ClientTickEvent;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.task.forge.TaskExecutor;
import net.sushiclient.client.task.tasks.TransactionWaitTask;
import net.sushiclient.client.utils.player.InventoryType;
import net.sushiclient.client.utils.player.InventoryUtils;
import net.sushiclient.client.utils.player.ItemSlot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AutoArmorModule extends BaseModule {

    private static final EntityEquipmentSlot[] SLOTS = {
            EntityEquipmentSlot.FEET,
            EntityEquipmentSlot.LEGS,
            EntityEquipmentSlot.CHEST,
            EntityEquipmentSlot.HEAD
    };

    private final Configuration<Boolean> preferThorns;
    private final Configuration<Boolean> preferElytra;
    private boolean running;

    public AutoArmorModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        preferThorns = provider.get("prefer_thorns", "Prefer Thorns", null, Boolean.class, true);
        preferElytra = provider.get("prefer_elytra", "Prefer Elytra", null, Boolean.class, false);
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
    }

    private int getScore(ItemSlot slot) {
        int score = 0;
        ItemStack itemStack = slot.getItemStack();
        Item item = slot.getItemStack().getItem();

        // material
        if (item.equals(Item.getItemById(443)) && preferElytra.getValue()) score += 5;
        if (item instanceof ItemArmor) {
            ItemArmor itemArmor = (ItemArmor) slot.getItemStack().getItem();
            ItemArmor.ArmorMaterial material = itemArmor.getArmorMaterial();
            if (material == ItemArmor.ArmorMaterial.DIAMOND) score += 4;
            else if (material == ItemArmor.ArmorMaterial.IRON) score += 3;
            else if (material == ItemArmor.ArmorMaterial.CHAIN) score += 2;
            else if (material == ItemArmor.ArmorMaterial.GOLD) score += 1;
        }

        // enchants
        NBTTagList enchants = itemStack.getEnchantmentTagList();
        boolean hasMainEnchants = false;
        int mainEnchantLevel = 0;
        boolean hasThorns = false;
        int totalEnchantLevels = 0;
        for (NBTBase enchantBase : enchants) {
            NBTTagCompound enchant = (NBTTagCompound) enchantBase;
            int id = enchant.getInteger("id");
            int level = enchant.getInteger("lvl");
            totalEnchantLevels += level;
            // protection, fire protection, blast protection, projectile protection
            if (id == 0 || id == 1 || id == 3 || id == 4) {
                hasMainEnchants = true;
                mainEnchantLevel = level;
            }
            if (id == 7) {
                hasThorns = true;
            }
        }

        score *= 10;
        if (hasMainEnchants) score += mainEnchantLevel;

        score *= 10;
        if (preferThorns.getValue() && hasThorns) score += 1;
        if (!preferThorns.getValue() && !hasThorns) score += 1;

        score *= 100;
        score += totalEnchantLevels;

        return score;
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onClientTick(ClientTickEvent e) {
        if (getClient().currentScreen instanceof GuiContainer) return;
        if (running) return;
        for (int i = 0; i < 4; i++) {
            EntityEquipmentSlot equipmentSlot = SLOTS[i];
            int armorSlot = i + InventoryType.ARMOR.getIndex();
            ArrayList<ItemSlot> items = new ArrayList<>();
            for (int j = 0; j < 40; j++) {
                ItemStack itemStack = getPlayer().inventory.getStackInSlot(j);
                if (!EntityLiving.getSlotForItemStack(itemStack).equals(equipmentSlot)) continue;
                items.add(new ItemSlot(j, getPlayer()));
            }
            if (items.isEmpty()) continue;
            items.sort(Comparator.comparingInt(this::getScore));
            Collections.reverse(items);
            int index = items.get(0).getIndex();
            if (InventoryType.ARMOR.getIndex() <= index &&
                    index < InventoryType.ARMOR.getIndex() + InventoryType.ARMOR.getSize()) continue;
            running = true;
            if (getPlayer().inventory.getStackInSlot(armorSlot).isEmpty()) {
                TaskExecutor.newTaskChain()
                        .then(() -> InventoryUtils.clickItemSlot(items.get(0), ClickType.QUICK_MOVE, 0))
                        .last(() -> running = false)
                        .execute();
            } else {
                TaskExecutor.newTaskChain()
                        .supply(() -> InventoryUtils.clickItemSlot(items.get(0), ClickType.PICKUP, 0))
                        .then(new TransactionWaitTask())
                        .supply(() -> InventoryUtils.clickItemSlot(new ItemSlot(armorSlot, getPlayer()), ClickType.PICKUP, 0))
                        .then(new TransactionWaitTask())
                        .then(() -> InventoryUtils.clickItemSlot(items.get(0), ClickType.PICKUP, 0))
                        .last(() -> running = false)
                        .execute();
            }
            return;
        }
    }

    @Override
    public String getDefaultName() {
        return "AutoArmor";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.PLAYER;
    }
}
