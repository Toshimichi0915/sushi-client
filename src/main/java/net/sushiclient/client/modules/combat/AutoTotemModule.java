package net.sushiclient.client.modules.combat;

import net.minecraft.init.Items;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.config.data.IntRange;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.tick.ClientTickEvent;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.utils.UpdateTimer;
import net.sushiclient.client.utils.player.InventoryType;
import net.sushiclient.client.utils.player.InventoryUtils;
import net.sushiclient.client.utils.player.ItemSlot;

public class AutoTotemModule extends BaseModule {

    private final Configuration<IntRange> delay;
    private final UpdateTimer timer;

    public AutoTotemModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        delay = provider.get("delay", "Delay", null, IntRange.class, new IntRange(1, 10, 0, 1));
        timer = new UpdateTimer(false, delay);
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
        ItemSlot offhand = ItemSlot.offhand();
        if (offhand.getItemStack().getItem().equals(Items.TOTEM_OF_UNDYING)) return;
        ItemSlot itemSlot = InventoryUtils.findItemSlot(Items.TOTEM_OF_UNDYING, InventoryType.values());
        if (itemSlot == null) return;
        if (!timer.update()) return;
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
