package net.toshimichi.sushi.modules.combat;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.toshimichi.sushi.config.RootConfigurations;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.tick.ClientTickEvent;
import net.toshimichi.sushi.modules.*;
import net.toshimichi.sushi.task.forge.TaskExecutor;
import net.toshimichi.sushi.task.tasks.BlockPlaceTask;
import net.toshimichi.sushi.task.tasks.ItemSwitchTask;
import net.toshimichi.sushi.utils.EntityInfo;
import net.toshimichi.sushi.utils.EntityUtils;
import net.toshimichi.sushi.utils.combat.DamageUtils;
import net.toshimichi.sushi.utils.player.DesyncMode;
import net.toshimichi.sushi.utils.player.PositionUtils;
import net.toshimichi.sushi.utils.world.BlockFace;
import net.toshimichi.sushi.utils.world.BlockPlaceInfo;
import net.toshimichi.sushi.utils.world.BlockUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AntiPistonAuraModule extends BaseModule {

    private final ArrayList<BlockPlaceInfo> spam = new ArrayList<>();
    private long when;

    public AntiPistonAuraModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
        spam.clear();
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
    }

    private EntityEnderCrystal getNearbyCrystal(Vec3d vec) {
        List<EntityInfo<EntityEnderCrystal>> crystals = EntityUtils.getNearbyEntities(vec, EntityEnderCrystal.class);
        if (crystals.isEmpty()) return null;
        EntityInfo<EntityEnderCrystal> candidate = crystals.get(0);
        if (candidate.getDistanceSq() > 3) return null;
        else return candidate.getEntity();
    }

    private BlockPlaceInfo findBlockPlaceInfo(World world, BlockPos input) {
        for (EnumFacing facing : EnumFacing.values()) {
            BlockPlaceInfo info = new BlockFace(input.offset(facing), facing.getOpposite()).toBlockPlaceInfo(world);
            BlockPos pos = info.getBlockPos();
            if (BlockUtils.isAir(world, pos.offset(facing))) continue;
            return info;
        }
        return null;
    }

    private boolean processPosition(BlockPos pos) {
        IBlockState blockState = getWorld().getBlockState(pos);
        if (!(blockState.getBlock() instanceof BlockPistonBase)) return false;
        EnumFacing enumFacing = blockState.getValue(BlockDirectional.FACING);
        EntityEnderCrystal crystal = getNearbyCrystal(BlockUtils.toVec3d(pos.offset(enumFacing)).add(0.5, 0, 0.5));
        if (crystal == null) return false;
        Vec3d predicted = crystal.getPositionVector().add(new Vec3d(enumFacing.getDirectionVec()).scale(0.5));
        if (DamageUtils.getCrystalDamage(getPlayer(), predicted) < 50) return false;
        spam.add(findBlockPlaceInfo(getWorld(), pos));
        if (!BlockUtils.isAir(getWorld(), pos.offset(enumFacing).offset(enumFacing))) {
            spam.add(findBlockPlaceInfo(getWorld(), pos.offset(enumFacing)));
        }
        when = System.currentTimeMillis();
        PositionUtils.desync(DesyncMode.ALL);
        PositionUtils.move(getPlayer().posX, getPlayer().posY + 0.2, getPlayer().posZ, 0, 0, true, false, DesyncMode.POSITION);
        PositionUtils.lookAt(crystal.getPositionVector(), DesyncMode.LOOK);
        getConnection().sendPacket(new CPacketUseEntity(crystal));
        PositionUtils.pop();

        return true;
    }

    @EventHandler(timing = EventTiming.POST)
    public void onClientTick(ClientTickEvent e) {
        spam.removeIf(it -> {
            Block block = getWorld().getBlockState(it.getBlockPos()).getBlock();
            if (block != Blocks.PISTON && block != Blocks.PISTON_HEAD && block != Blocks.PISTON_EXTENSION && block != Blocks.AIR ||
                    System.currentTimeMillis() - when > 1000) {
                return true;
            } else {
                TaskExecutor.newTaskChain()
                        .supply(() -> Item.getItemFromBlock(Blocks.OBSIDIAN))
                        .then(new ItemSwitchTask(null, false))
                        .abortIfFalse()
                        .supply(() -> Collections.singletonList(it))
                        .then(new BlockPlaceTask(true, true))
                        .execute();
                return false;
            }
        });
        BlockPos playerPos = BlockUtils.toBlockPos(getPlayer().getPositionVector());
        for (int x = -3; x < 4; x++) {
            for (int y = 1; y < 4; y++) {
                for (int z = -3; z < 4; z++) {
                    BlockPos pos = new BlockPos(playerPos.getX() + x, playerPos.getY() + y, playerPos.getZ() + z);
                    if (processPosition(pos)) break;
                }
            }
        }
    }

    @Override
    public String getDefaultName() {
        return "AntiPistonAura";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.COMBAT;
    }
}
