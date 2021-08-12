package net.sushiclient.client.modules.combat;

import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.item.Item;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.config.data.DoubleRange;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.tick.ClientTickEvent;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.utils.EntityInfo;
import net.sushiclient.client.utils.EntityUtils;
import net.sushiclient.client.utils.combat.DamageUtils;
import net.sushiclient.client.utils.player.InventoryType;
import net.sushiclient.client.utils.player.InventoryUtils;
import net.sushiclient.client.utils.player.ItemSlot;

public class OffhandModule extends BaseModule {

    private final Configuration<SwitchTarget> defaultItem;
    private final Configuration<DoubleRange> totemHelth;
    private final Configuration<Boolean> crystalCheck;
    //    private final Configuration<Boolean> rightClickGap;
//    private final Configuration<Boolean> fallCheck;
    private final Configuration<Boolean> totemOnElytra;
    private final Configuration<Boolean> preferInventory;

    public OffhandModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        defaultItem = provider.get("default_item", "Default Item", null, SwitchTarget.class, SwitchTarget.TOTEM);
        totemHelth = provider.get("totem_health", "Totem Health", null, DoubleRange.class, new DoubleRange(5, 20, 0, 0.1, 1));
        crystalCheck = provider.get("crystal_check", "Crystal Check", null, Boolean.class, true);
//        rightClickGap = provider.get("right_click_gapple", "Right Click Gappple", null, Boolean.class, true);
//        fallCheck = provider.get("fall_check", "Fall Check", null, Boolean.class, true);
        totemOnElytra = provider.get("totem_on_elytra", "Totem On Elytra", null, Boolean.class, true);
        preferInventory = provider.get("prefer_inventory", "Prefer Inventory", null, Boolean.class, true);
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
    }

    private double getHealth() {
        return getPlayer().getHealth() + getPlayer().getAbsorptionAmount();
    }

    private double getCrystalDamage() {
        double max = 0;
        for (EntityInfo<EntityEnderCrystal> info : EntityUtils.getNearbyEntities(getPlayer().getPositionVector(), EntityEnderCrystal.class)) {
            double damage = DamageUtils.applyModifier(getPlayer(), DamageUtils.getCrystalDamage(getPlayer(), info.getEntity().getPositionVector()), DamageUtils.EXPLOSION);
            if (damage > max) max = damage;
        }
        return max;
    }

    public SwitchTarget getSwitchTarget() {
        if (totemOnElytra.getValue() && getPlayer().isElytraFlying()) {
            return SwitchTarget.TOTEM;
        } else if (getHealth() < totemHelth.getValue().getCurrent()) {
            return SwitchTarget.TOTEM;
        } else if (crystalCheck.getValue() && getHealth() - getCrystalDamage() < totemHelth.getValue().getCurrent()) {
            return SwitchTarget.TOTEM;
        }

        return defaultItem.getValue();
    }

    public SwitchTarget getCurrent() {
        Item item = ItemSlot.offhand().getItemStack().getItem();
        for (SwitchTarget switchTarget : SwitchTarget.values()) {
            if (switchTarget.getItem().equals(item)) return switchTarget;
        }
        return null;
    }

    @EventHandler(timing = EventTiming.POST)
    public void onClientTick(ClientTickEvent e) {
        SwitchTarget target = getSwitchTarget();
        SwitchTarget current = getCurrent();
        if (target == current) return;
        ItemSlot itemSlot = null;
        if (preferInventory.getValue()) {
            itemSlot = InventoryUtils.findItemSlot(target.getItem(), InventoryType.MAIN);
        }
        if (itemSlot == null) {
            itemSlot = InventoryUtils.findItemSlot(target.getItem(), InventoryType.values());
        }
        if (itemSlot == null) {
            return;
        }
        InventoryUtils.moveTo(itemSlot, ItemSlot.offhand());
    }

    @Override
    public String getDefaultName() {
        return "Offhand";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.COMBAT;
    }

}
