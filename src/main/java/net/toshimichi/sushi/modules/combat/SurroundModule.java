package net.toshimichi.sushi.modules.combat;

import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.Configurations;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.player.PlayerUpdateEvent;
import net.toshimichi.sushi.modules.*;
import net.toshimichi.sushi.task.forge.TaskExecutor;
import net.toshimichi.sushi.task.tasks.BlockPlaceTask;
import net.toshimichi.sushi.utils.*;

import java.util.ArrayList;
import java.util.List;

public class SurroundModule extends BaseModule {

    private final Configuration<Boolean> pull;
    private final Configuration<Boolean> disableAfter;
    private final Configuration<Boolean> disableOnJump;
    private boolean running;

    public SurroundModule(String id, Modules modules, Categories categories, Configurations provider, ModuleFactory factory) {
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
        Item obsidian = Item.getItemById(49);
        List<Integer> hotbar = InventoryUtils.findItemFromHotbar(obsidian);
        if (hotbar.isEmpty()) return;
        BlockPos pos = new BlockPos(getPlayer().posX, getPlayer().posY, getPlayer().posZ);
        if (pull.getValue()) {
            PositionUtils.move(pos.getX() + 0.5, getPlayer().posY, pos.getZ() + 0.5,
                    0, 0, true, false, DesyncMode.NONE);
        }
        if (disableOnJump.getValue() && getPlayer().movementInput.jump || disableAfter.getValue()) {
            setEnabled(false);
        }
        ArrayList<BlockPlaceInfo> toBePlaced = new ArrayList<>();
        for (EnumFacing facing : EnumFacing.values()) {
            BlockPos neighbor = pos.offset(facing);
            BlockPlaceInfo neighborFace = BlockUtils.findFace(getWorld(), neighbor);
            if (neighborFace == null) {
                BlockPos under = neighbor.offset(EnumFacing.DOWN);
                BlockPlaceInfo underFace = BlockUtils.findFace(getWorld(), under);
                if (underFace == null) continue;
                toBePlaced.add(underFace);
                toBePlaced.add(new BlockPlaceInfo(neighbor, new BlockFace(new Vec3d(0.5, 1, 0.5), EnumFacing.UP)));
            } else {
                toBePlaced.add(neighborFace);
            }
        }
        getPlayer().inventory.currentItem = hotbar.get(0);
        getController().updateController();

        running = true;
        TaskExecutor.newTaskChain()
                .supply(true, () -> toBePlaced)
                .then(true, new BlockPlaceTask(DesyncMode.LOOK))
                .then(true, () -> running = false)
                .execute();
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onPlayerUpdate(PlayerUpdateEvent e) {
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
