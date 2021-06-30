package net.toshimichi.sushi.modules.combat;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.RootConfigurations;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.player.PlayerPacketEvent;
import net.toshimichi.sushi.modules.*;
import net.toshimichi.sushi.task.forge.TaskExecutor;
import net.toshimichi.sushi.task.tasks.BlockPlaceTask;
import net.toshimichi.sushi.task.tasks.ItemSwitchTask;
import net.toshimichi.sushi.utils.player.DesyncMode;
import net.toshimichi.sushi.utils.player.PositionUtils;
import net.toshimichi.sushi.utils.world.BlockPlaceInfo;
import net.toshimichi.sushi.utils.world.BlockPlaceUtils;

import java.util.ArrayList;
import java.util.List;

public class SurroundModule extends BaseModule {

    private final Configuration<Boolean> pull;
    private final Configuration<Boolean> disableAfter;
    private final Configuration<Boolean> disableOnJump;
    private boolean running;

    public SurroundModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        pull = provider.get("pull", "Pull", null, Boolean.class, true);
        disableAfter = provider.get("disable_after", "Disable After", null, Boolean.class, false);
        disableOnJump = provider.get("disable_on_jump", "Disable On Jump", null, Boolean.class, false);
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

    private void surround() {
        if (running) return;
        BlockPos pos = new BlockPos(getPlayer().posX, getPlayer().posY, getPlayer().posZ);
        if (disableOnJump.getValue() && getPlayer().movementInput.jump || disableAfter.getValue()) {
            setEnabled(false);
        }
        ArrayList<BlockPlaceInfo> placeList = new ArrayList<>();
        for (EnumFacing facing : EnumFacing.values()) {
            if (facing == EnumFacing.UP) continue;
            List<BlockPlaceInfo> info = BlockPlaceUtils.search(getWorld(), pos.offset(facing), 3);
            if (info == null) continue;
            placeList.addAll(info);
        }
        if (placeList.isEmpty()) return;
        if (pull.getValue()) {
            PositionUtils.move(pos.getX() + 0.5, getPlayer().posY, pos.getZ() + 0.5,
                    0, 0, true, false, DesyncMode.NONE);
        }
        running = true;
        TaskExecutor.newTaskChain()
                .supply(() -> Item.getItemFromBlock(Blocks.OBSIDIAN))
                .then(new ItemSwitchTask(null, true))
                .abortIfFalse()
                .supply(() -> placeList)
                .then(new BlockPlaceTask(true, true))
                .last(() -> running = false)
                .execute();
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onPlayerUpdate(PlayerPacketEvent e) {
        surround();
    }

    @Override
    public String getDefaultName() {
        return "Surround";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.COMBAT;
    }
}
