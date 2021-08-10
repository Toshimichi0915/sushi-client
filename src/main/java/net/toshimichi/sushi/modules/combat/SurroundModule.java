package net.toshimichi.sushi.modules.combat;

import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
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
import net.toshimichi.sushi.utils.EntityInfo;
import net.toshimichi.sushi.utils.EntityUtils;
import net.toshimichi.sushi.utils.player.*;
import net.toshimichi.sushi.utils.world.BlockPlaceInfo;
import net.toshimichi.sushi.utils.world.BlockPlaceUtils;
import net.toshimichi.sushi.utils.world.BlockUtils;
import net.toshimichi.sushi.utils.world.PlaceOptions;

import java.util.*;

public class SurroundModule extends BaseModule {

    private final Configuration<Boolean> pull;
    private final Configuration<Boolean> disableAfter;
    private final Configuration<Boolean> disableOnJump;
    private final Configuration<Boolean> packetPlace;
    private final Configuration<Boolean> antiGhostBlock;
    private final Collection<Vec3i> checked = Collections.newSetFromMap(new WeakHashMap<>());
    private boolean running;

    public SurroundModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        pull = provider.get("pull", "Pull", null, Boolean.class, true);
        disableAfter = provider.get("disable_after", "Disable After", null, Boolean.class, false);
        disableOnJump = provider.get("disable_on_jump", "Disable On Jump", null, Boolean.class, false);
        packetPlace = provider.get("packet_place", "Packet Place", null, Boolean.class, true);
        antiGhostBlock = provider.get("anti_ghost_block", "Anti Ghost Block", null, Boolean.class, true, () -> !packetPlace.getValue(), false, 0);
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
        BlockPos pos = BlockUtils.toBlockPos(getPlayer().getPositionVector());
        if (disableOnJump.getValue() && getPlayer().movementInput.jump || disableAfter.getValue()) {
            setEnabled(false);
        }
        ArrayList<BlockPlaceInfo> placeList = new ArrayList<>();
        ArrayList<BlockPos> toBeChecked = new ArrayList<>(4);
        for (EnumFacing facing : EnumFacing.values()) {
            if (facing == EnumFacing.UP) continue;
            BlockPos offset = pos.offset(facing);
            if (antiGhostBlock.isValid() && antiGhostBlock.getValue() && !checked.contains(offset)) {
                toBeChecked.add(offset);
                checked.add(offset);
            }
            List<BlockPlaceInfo> info = BlockPlaceUtils.search(getWorld(), offset, 3, PlaceOptions.IGNORE_ENTITY);
            if (info == null) continue;
            placeList.addAll(info);
        }
        BlockUtils.checkGhostBlock(toBeChecked.toArray(new BlockPos[0]));
        placeList.removeIf(it -> {
            AxisAlignedBB box = getWorld().getBlockState(it.getBlockPos()).getBoundingBox(getWorld(), it.getBlockPos()).offset(it.getBlockPos());
            for (EntityInfo<EntityEnderCrystal> crystal : EntityUtils.getNearbyEntities(Vec3d.ZERO, EntityEnderCrystal.class)) {
                if (crystal.getEntity().getEntityBoundingBox().intersects(box)) return true;
            }
            return false;
        });
        if (placeList.isEmpty()) return;
        ItemSlot obsidianSlot = InventoryUtils.findItemSlot(Item.getItemFromBlock(Blocks.OBSIDIAN), InventoryType.values());
        if (obsidianSlot == null) return;
        if (pull.getValue()) {
            PositionUtils.move(pos.getX() + 0.5, getPlayer().posY, pos.getZ() + 0.5,
                    0, 0, true, false, DesyncMode.NONE);
        }
        running = true;
        TaskExecutor.newTaskChain()
                .supply(Item.getItemFromBlock(Blocks.OBSIDIAN))
                .then(new ItemSwitchTask(null, true))
                .abortIfFalse()
                .supply(placeList)
                .then(new BlockPlaceTask(true, true, packetPlace.getValue()))
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
