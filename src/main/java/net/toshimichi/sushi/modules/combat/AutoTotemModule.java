package net.toshimichi.sushi.modules.combat;

import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.RootConfigurations;
import net.toshimichi.sushi.config.data.IntRange;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.tick.ClientTickEvent;
import net.toshimichi.sushi.modules.*;
import net.toshimichi.sushi.utils.TickUtils;
import net.toshimichi.sushi.utils.player.InventoryType;
import net.toshimichi.sushi.utils.player.InventoryUtils;
import net.toshimichi.sushi.utils.player.ItemSlot;

public class AutoTotemModule extends BaseModule {

    private final Configuration<IntRange> delay;
    private int lastUpdate;

    public AutoTotemModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        delay = provider.get("delay", "Delay", null, IntRange.class, new IntRange(1, 10, 0, 1));
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onClientTick(ClientTickEvent e) {
        if (lastUpdate > TickUtils.current() + delay.getValue().getCurrent()) return;
        int offhandSlot = InventoryType.OFFHAND.getIndex();
        ItemStack offhand = getPlayer().inventory.getStackInSlot(offhandSlot);
        Item totem = Item.getItemById(449);
        if (offhand.getItem().equals(totem)) return;
        ItemSlot itemSlot = InventoryUtils.findItemSlot(totem, getPlayer(), InventoryType.values());
        if (itemSlot == null) return;
        lastUpdate = TickUtils.current();
        InventoryUtils.clickItemSlot(itemSlot, ClickType.PICKUP, 0);
        InventoryUtils.clickItemSlot(new ItemSlot(offhandSlot, getPlayer()), ClickType.PICKUP, 0);
    }

    @Override
    public String getDefaultName() {
        return "AutoTotem";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.COMBAT;
    }
}
