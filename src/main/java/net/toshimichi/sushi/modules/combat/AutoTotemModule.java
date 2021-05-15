package net.toshimichi.sushi.modules.combat;

import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.Configurations;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.tick.ClientTickEvent;
import net.toshimichi.sushi.modules.*;
import net.toshimichi.sushi.task.forge.TaskExecutor;
import net.toshimichi.sushi.task.tasks.TransactionWaitTask;
import net.toshimichi.sushi.utils.InventoryType;
import net.toshimichi.sushi.utils.InventoryUtils;
import net.toshimichi.sushi.utils.ItemSlot;

public class AutoTotemModule extends BaseModule {

    private final Configuration<Boolean> confirm;
    private final Configuration<Integer> delay;
    private boolean running;

    public AutoTotemModule(String id, Modules modules, Categories categories, Configurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        confirm = provider.get("confirm", "Confirm", null, Boolean.class, true);
        delay = provider.get("delay", "Delay", null, Integer.class, 1, confirm::getValue, null, false, 0);
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
    }

    @Override
    public void onDisable() {
        running = false;
        EventHandlers.unregister(this);
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onClientTick(ClientTickEvent e) {
        if (running) return;
        int offhandSlot = InventoryType.OFFHAND.getIndex();
        ItemStack offhand = getPlayer().inventory.getStackInSlot(offhandSlot);
        Item totem = Item.getItemById(449);
        if (offhand.getItem().equals(totem)) return;
        ItemSlot itemSlot = InventoryUtils.findItemSlot(totem, getPlayer(), InventoryType.values());
        if (itemSlot == null) return;
        running = true;
        if (confirm.getValue()) {
            TaskExecutor.newTaskChain()
                    .supply(() -> InventoryUtils.clickItemSlot(itemSlot, ClickType.PICKUP, 0))
                    .then(new TransactionWaitTask())
                    .then(() -> InventoryUtils.clickItemSlot(new ItemSlot(offhandSlot, getPlayer()), ClickType.PICKUP, 0))
                    .then(() -> running = false)
                    .execute();
        } else {
            TaskExecutor.newTaskChain()
                    .then(() -> InventoryUtils.clickItemSlot(itemSlot, ClickType.PICKUP, 0))
                    .delay(delay.getValue())
                    .then(() -> InventoryUtils.clickItemSlot(new ItemSlot(offhandSlot, getPlayer()), ClickType.PICKUP, 0))
                    .then(() -> running = false)
                    .execute();
        }
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
