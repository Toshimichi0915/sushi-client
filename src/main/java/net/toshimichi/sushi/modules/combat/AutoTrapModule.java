package net.toshimichi.sushi.modules.combat;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.toshimichi.sushi.config.RootConfigurations;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.tick.ClientTickEvent;
import net.toshimichi.sushi.modules.*;
import net.toshimichi.sushi.task.forge.TaskExecutor;
import net.toshimichi.sushi.task.tasks.BlockPlaceTask;
import net.toshimichi.sushi.task.tasks.ItemSwitchTask;
import net.toshimichi.sushi.utils.EntityUtils;
import net.toshimichi.sushi.utils.world.BlockPlaceInfo;
import net.toshimichi.sushi.utils.world.BlockPlaceUtils;
import net.toshimichi.sushi.utils.world.BlockUtils;

import java.util.ArrayList;
import java.util.List;

public class AutoTrapModule extends BaseModule {

    private boolean running;

    public AutoTrapModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
        running = false;
    }

    @EventHandler(timing = EventTiming.POST)
    public void onClientTick(ClientTickEvent e) {
        if (running) return;
        List<EntityPlayer> nearbyPlayers = EntityUtils.getNearbyPlayers(6);
        if (nearbyPlayers.isEmpty()) return;
        EntityPlayer closest = nearbyPlayers.get(0);
        ArrayList<BlockPos> placeList = new ArrayList<>();
        for (EnumFacing facing : new EnumFacing[]{EnumFacing.DOWN, EnumFacing.EAST, EnumFacing.NORTH, EnumFacing.WEST, EnumFacing.SOUTH}) {
            BlockPos pos = BlockUtils.toBlockPos(closest.getPositionVector()).offset(facing);
            if (BlockUtils.isAir(getWorld(), pos)) {
                placeList.add(pos);
            }
        }
        for (EnumFacing facing : new EnumFacing[]{EnumFacing.UP, EnumFacing.EAST, EnumFacing.NORTH, EnumFacing.WEST, EnumFacing.SOUTH}) {
            BlockPos pos = BlockUtils.toBlockPos(closest.getPositionVector()).offset(facing).add(0, 1, 0);
            if (BlockUtils.isAir(getWorld(), pos)) {
                placeList.add(pos);
            }
        }
        if (placeList.isEmpty()) return;
        List<BlockPlaceInfo> target = null;
        for (BlockPos pos : placeList) {
            List<BlockPlaceInfo> info = BlockPlaceUtils.search(getWorld(), pos, 3);
            if (info != null) {
                target = info;
                break;
            }
        }
        if (target == null) return;
        running = true;
        TaskExecutor.newTaskChain()
                .supply(Item.getItemFromBlock(Blocks.OBSIDIAN))
                .then(new ItemSwitchTask(null, true))
                .abortIfFalse()
                .supply(target)
                .then(new BlockPlaceTask(true, true))
                .last(() -> running = false)
                .execute();
    }

    @Override
    public String getDefaultName() {
        return "AutoTrap";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.COMBAT;
    }
}
