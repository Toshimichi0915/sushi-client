package net.sushiclient.client.modules.world;

import net.minecraft.item.ItemBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.sushiclient.client.config.Config;
import net.sushiclient.client.config.ConfigInjector;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.config.data.IntRange;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.player.PlayerTravelEvent;
import net.sushiclient.client.events.tick.ClientTickEvent;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.task.forge.TaskExecutor;
import net.sushiclient.client.task.tasks.BlockPlaceTask;
import net.sushiclient.client.utils.player.InventoryType;
import net.sushiclient.client.utils.player.InventoryUtils;
import net.sushiclient.client.utils.player.ItemSlot;
import net.sushiclient.client.utils.world.BlockPlaceInfo;
import net.sushiclient.client.utils.world.BlockPlaceUtils;
import net.sushiclient.client.utils.world.BlockUtils;

import java.util.List;

public class ScaffoldModule extends BaseModule {

    @Config(id = "switch", name = "Switch")
    public Boolean autoSwitch = true;

    @Config(id = "refill", name = "Refill")
    public Boolean refill = true;

    @Config(id = "threshold", name = "Threshold")
    public IntRange threshold = new IntRange(32, 64, 1, 1);

    private boolean hasBlock;

    public ScaffoldModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        new ConfigInjector(provider).inject(this);
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
    public void onPlayerTravel(PlayerTravelEvent e) {
        BlockPos floor = BlockUtils.toBlockPos(getPlayer().getPositionVector()).add(0, -1, 0);
        if (BlockUtils.isAir(getWorld(), floor)) return;

        if (getPlayer().movementInput.jump && hasBlock && getPlayer().motionY < 0.21) {
            getPlayer().motionY = 0.42;
        }
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onClientTick(ClientTickEvent e) {
        Vec3d floor = getPlayer().getPositionVector().add(0, -1, 0);
        BlockPos floorPos = BlockUtils.toBlockPos(floor);
        hasBlock = false;
        if (ItemSlot.current().getItemStack().getItem() instanceof ItemBlock) {
            hasBlock = true;
            ItemSlot slot = InventoryType.MAIN.findStackable(ItemSlot.current().getItemStack());
            if (refill && ItemSlot.current().getItemStack().getCount() < threshold.getCurrent() && slot != null) {
                InventoryUtils.moveTo(slot, ItemSlot.current());
            }
        } else if (autoSwitch) {
            for (ItemSlot itemSlot : InventoryType.HOTBAR) {
                if (itemSlot.getItemStack().getItem() instanceof ItemBlock) {
                    InventoryUtils.moveHotbar(itemSlot.getIndex());
                    hasBlock = true;
                }
            }
        }
        List<BlockPlaceInfo> tasks = BlockPlaceUtils.search(getWorld(), floorPos, 3);
        if (tasks == null) return;
        TaskExecutor.newTaskChain()
                .supply(tasks)
                .then(new BlockPlaceTask(true, true))
                .execute();
    }

    @Override
    public String getDefaultName() {
        return "Scaffold";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.WORLD;
    }
}
