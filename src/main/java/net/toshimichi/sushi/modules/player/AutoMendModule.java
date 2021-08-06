package net.toshimichi.sushi.modules.player;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.toshimichi.sushi.Sushi;
import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.RootConfigurations;
import net.toshimichi.sushi.config.data.IntRange;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.tick.ClientTickEvent;
import net.toshimichi.sushi.modules.*;
import net.toshimichi.sushi.utils.EntityInfo;
import net.toshimichi.sushi.utils.EntityUtils;
import net.toshimichi.sushi.utils.combat.DamageUtils;
import net.toshimichi.sushi.utils.player.*;

import java.util.List;

public class AutoMendModule extends BaseModule {

    private final Configuration<IntRange> delay;
    private final Configuration<Boolean> repairAll;
    private final Configuration<Boolean> autoDisable;
    private final Configuration<Boolean> crystalCheck;
    private final Configuration<IntRange> selfHp;
    private int lastDelayTicks;

    private static final EntityEquipmentSlot[] SLOTS = {
            EntityEquipmentSlot.FEET,
            EntityEquipmentSlot.LEGS,
            EntityEquipmentSlot.CHEST,
            EntityEquipmentSlot.HEAD
    };

    public AutoMendModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        delay = provider.get("delay", "Delay", null, IntRange.class, new IntRange(0, 10, 0, 1));
        repairAll = provider.get("repair_all", "Repair All", null, Boolean.class, true);
        autoDisable = provider.get("auto_disable", "Auto Disable", null, Boolean.class, true);
        crystalCheck = provider.get("crystal_check", "Crystal Check", null, Boolean.class, true);
        selfHp = provider.get("slef_hp", "Self HP", null, IntRange.class, new IntRange(6, 20, 0, 1));
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
        for (Module module : Sushi.getProfile().getModules().getAll()) {
            if (!(module instanceof AutoArmorModule)) continue;
            module.setPaused(false);
        }
    }

    private EntityEquipmentSlot getEntityEquipmentSlot(ItemSlot itemSlot) {
        if (itemSlot.getInventoryType() != InventoryType.ARMOR) return null;
        return SLOTS[itemSlot.getIndex() - InventoryType.ARMOR.getIndex()];
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onClientTick(ClientTickEvent e) {
        if (lastDelayTicks-- > 0) return;
        ItemSlot expBottle = InventoryUtils.findItemSlot(Items.EXPERIENCE_BOTTLE, getPlayer(), InventoryType.values());
        List<EntityInfo<EntityEnderCrystal>> crystals = EntityUtils.getNearbyEntities(getPlayer().getPositionVector(), EntityEnderCrystal.class);
        double crystalDamage;
        if (!crystalCheck.getValue() || crystals.isEmpty()) {
            crystalDamage = 0;
        } else {
            crystalDamage = DamageUtils.getCrystalDamage(getPlayer(), crystals.get(0).getEntity().getPositionVector());
            crystalDamage = DamageUtils.applyModifier(getPlayer(), crystalDamage, DamageUtils.EXPLOSION);
        }
        if (expBottle == null ||
                getPlayer().getHealth() + getPlayer().getAbsorptionAmount() - crystalDamage < selfHp.getValue().getCurrent()) {
            setEnabled(false);
            return;
        }

        // pause auto armor
        for (Module module : Sushi.getProfile().getModules().getAll()) {
            if (!(module instanceof AutoArmorModule)) continue;
            module.setPaused(true);
        }

        boolean canRepair = false;
        // switching
        for (ItemSlot itemSlot : InventoryType.ARMOR) {

            boolean switching = false;
            if (ItemUtils.getEnchantmentLevel(itemSlot.getItemStack(), Enchantments.MENDING) == 0 ||
                    (repairAll.getValue() && itemSlot.getItemStack().getItemDamage() == 0)) {
                // find alternatives
                for (ItemSlot armor : InventoryType.MAIN) {
                    ItemStack armorItem = armor.getItemStack();
                    if (!(armorItem.getItem() instanceof ItemArmor)) continue;
                    if (EntityLiving.getSlotForItemStack(armorItem) != getEntityEquipmentSlot(itemSlot)) continue;
                    if (ItemUtils.getEnchantmentLevel(armorItem, Enchantments.MENDING) == 0) continue;
                    if (itemSlot.getItemStack().getItemDamage() == 0) continue;
                    InventoryUtils.moveTo(armor, itemSlot);
                    itemSlot = armor;
                    switching = true;
                    canRepair = true;
                    break;
                }

                // find empty slots
                ItemSlot emptySlot = InventoryUtils.findItemSlot(Items.AIR, getPlayer(), InventoryType.MAIN);
                if (!switching && itemSlot.getItemStack().getItem() != Items.AIR && emptySlot != null) {
                    InventoryUtils.moveTo(itemSlot, emptySlot);
                }
            } else {
                canRepair = true;
            }
        }

        // auto disable
        if (!canRepair && autoDisable.getValue()) {
            setEnabled(false);
            return;
        }

        lastDelayTicks = delay.getValue().getCurrent();
        InventoryUtils.moveToHotbar(expBottle);
        try (DesyncCloseable closeable = PositionUtils.desync(DesyncMode.LOOK)) {
            PositionUtils.move(Vec3d.ZERO, getPlayer().rotationYaw, -90, false, true, DesyncMode.LOOK);
            InventoryUtils.moveHotbar(expBottle.getIndex());
            getConnection().sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
        }
    }

    @Override
    public String getDefaultName() {
        return "AutoMend";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.PLAYER;
    }
}
