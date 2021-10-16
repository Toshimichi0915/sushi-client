package net.sushiclient.client.modules.combat;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.tick.ClientTickEvent;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.task.forge.TaskExecutor;
import net.sushiclient.client.task.tasks.BlockPlaceTask;
import net.sushiclient.client.task.tasks.ItemSwitchTask;
import net.sushiclient.client.utils.EntityUtils;
import net.sushiclient.client.utils.player.RotateMode;
import net.sushiclient.client.utils.world.BlockPlaceInfo;
import net.sushiclient.client.utils.world.BlockPlaceUtils;
import net.sushiclient.client.utils.world.BlockUtils;

import java.util.ArrayList;
import java.util.List;

public class AutoTrapModule extends BaseModule {

    private final Configuration<RotateMode> rotateMode;
    private boolean running;

    public AutoTrapModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        rotateMode = provider.get("rotate_mode", "Rotate Mode", null, RotateMode.class, RotateMode.NCP);
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
                .then(new BlockPlaceTask(rotateMode.getValue(), true, false, true))
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
