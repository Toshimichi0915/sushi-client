package net.toshimichi.sushi.modules.combat;

import net.minecraft.init.Items;
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
        ItemSlot offhand = ItemSlot.offhand();
        if (offhand.getItemStack().getItem().equals(Items.TOTEM_OF_UNDYING)) return;
        ItemSlot itemSlot = InventoryUtils.findItemSlot(Items.TOTEM_OF_UNDYING, getPlayer(), InventoryType.values());
        if (itemSlot == null) return;
        lastUpdate = TickUtils.current();
        InventoryUtils.moveTo(itemSlot, InventoryType.OFFHAND.getAll()[0]);
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
