package net.toshimichi.sushi.modules.world;

import net.minecraft.item.ItemBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.toshimichi.sushi.config.Config;
import net.toshimichi.sushi.config.ConfigInjector;
import net.toshimichi.sushi.config.RootConfigurations;
import net.toshimichi.sushi.config.data.IntRange;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.player.PlayerTravelEvent;
import net.toshimichi.sushi.events.tick.ClientTickEvent;
import net.toshimichi.sushi.modules.*;
import net.toshimichi.sushi.task.forge.TaskExecutor;
import net.toshimichi.sushi.task.tasks.BlockPlaceTask;
import net.toshimichi.sushi.utils.player.InventoryType;
import net.toshimichi.sushi.utils.player.InventoryUtils;
import net.toshimichi.sushi.utils.player.ItemSlot;
import net.toshimichi.sushi.utils.world.BlockPlaceInfo;
import net.toshimichi.sushi.utils.world.BlockPlaceUtils;
import net.toshimichi.sushi.utils.world.BlockUtils;

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
