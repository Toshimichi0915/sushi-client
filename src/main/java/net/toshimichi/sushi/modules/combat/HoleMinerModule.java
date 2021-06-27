package net.toshimichi.sushi.modules.combat;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.toshimichi.sushi.config.Config;
import net.toshimichi.sushi.config.ConfigInjector;
import net.toshimichi.sushi.config.RootConfigurations;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.tick.ClientTickEvent;
import net.toshimichi.sushi.events.world.WorldRenderEvent;
import net.toshimichi.sushi.modules.*;
import net.toshimichi.sushi.task.forge.TaskExecutor;
import net.toshimichi.sushi.task.tasks.ItemSwitchTask;
import net.toshimichi.sushi.utils.EntityUtils;
import net.toshimichi.sushi.utils.player.InventoryUtils;
import net.toshimichi.sushi.utils.player.ItemSlot;
import net.toshimichi.sushi.utils.player.ItemUtils;
import net.toshimichi.sushi.utils.render.RenderUtils;
import net.toshimichi.sushi.utils.world.BlockUtils;

import java.awt.Color;

public class HoleMinerModule extends BaseModule {

    private boolean running;
    private BlockPos breakingBlock;

    @Config(id = "hole_mine_mode", name = "Hole Mine Mode")
    public HoleMineMode holeMineMode = HoleMineMode.BEST_EFFORT;

    @Config(id = "disable_after", name = "Disable After")
    public Boolean disableAfter = false;

    public HoleMinerModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
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
        running = false;
        breakingBlock = null;
    }

    private boolean processHoleMine(EntityPlayer target, EnumFacing facing) {
        BlockPos playerPos = BlockUtils.toBlockPos(target.getPositionVector());
        BlockPos surroundPos = playerPos.offset(facing);
        BlockPos aboveSurroundPos = surroundPos.offset(EnumFacing.UP);
        BlockPos crystalPos = surroundPos.offset(facing);
        if (getWorld().getBlockState(surroundPos).getBlock() != Blocks.OBSIDIAN) return false;
        if (!BlockUtils.isAir(getWorld(), aboveSurroundPos) && !BlockUtils.isAir(getWorld(), crystalPos)) return false;
        running = true;
        breakingBlock = surroundPos;
        TaskExecutor.newTaskChain()
                .supply(() -> Items.DIAMOND_PICKAXE)
                .then(new ItemSwitchTask(null, true))
                .abortIf(found -> !found)
                .then(() -> getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, surroundPos, EnumFacing.DOWN)))
                .delay(() -> ItemUtils.getDestroyTime(surroundPos, ItemSlot.getCurrentItemSlot(getPlayer()).getItemStack()))
                .abortIf(() -> !running)

                .supply(() -> Items.DIAMOND_PICKAXE)
                .then(new ItemSwitchTask(null, true))
                .abortIf(found -> !found)
                .then(() -> getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, surroundPos, EnumFacing.DOWN)))
                .last(() -> {
                    running = false;
                    breakingBlock = null;
                    if (disableAfter) setEnabled(false);
                })
                .execute();

        return true;
    }

    private boolean processAntiSurround(EntityPlayer target, EnumFacing facing) {
        BlockPos playerPos = BlockUtils.toBlockPos(target.getPositionVector());
        BlockPos surroundPos = playerPos.offset(facing);
        BlockPos crystalPos = surroundPos.offset(facing);
        BlockPos crystalFloor = crystalPos.offset(EnumFacing.DOWN);
        if (getWorld().getBlockState(surroundPos).getBlock() != Blocks.OBSIDIAN) return false;
        if (getWorld().getBlockState(crystalFloor).getBlock() != Blocks.OBSIDIAN) return false;
        AxisAlignedBB crystalBox = new AxisAlignedBB(crystalPos.getX(), crystalPos.getY(), crystalPos.getZ(),
                crystalPos.getX() + 1, crystalPos.getY() + 2, crystalPos.getZ() + 1);
        if (BlockUtils.checkCollision(getWorld(), crystalBox)) return false;
        if (!EntityUtils.canInteract(BlockUtils.toVec3d(crystalPos).add(0.5, 0, 0.5), 6, 3)) return false;

        running = true;
        breakingBlock = surroundPos;
        TaskExecutor.newTaskChain()
                .supply(() -> Items.DIAMOND_PICKAXE)
                .then(new ItemSwitchTask(null, true))
                .abortIf(found -> !found)
                .then(() -> getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, surroundPos, EnumFacing.DOWN)))
                .delay(() -> ItemUtils.getDestroyTime(surroundPos, ItemSlot.getCurrentItemSlot(getPlayer()).getItemStack()))
                .abortIf(() -> !running)

                .supply(() -> Items.END_CRYSTAL)
                .then(new ItemSwitchTask(null, true))
                .abortIf(found -> !found)
                .then(() -> getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(crystalFloor, EnumFacing.DOWN, EnumHand.MAIN_HAND, 0.5F, 0, 0.5F)))

                .supply(() -> Items.DIAMOND_PICKAXE)
                .then(new ItemSwitchTask(null, true))
                .abortIf(found -> !found)
                .then(() -> getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, surroundPos, EnumFacing.DOWN)))
                .last(() -> {
                    running = false;
                    breakingBlock = null;
                    if (disableAfter) setEnabled(false);
                })
                .execute();
        return true;
    }


    @EventHandler(timing = EventTiming.POST)
    public void onClientTick(ClientTickEvent e) {
        if (running) return;
        if (!InventoryUtils.hasItem(Items.DIAMOND_PICKAXE)) return;
        if (InventoryUtils.hasItem(Items.END_CRYSTAL)) {
            for (EntityPlayer player : EntityUtils.getNearbyPlayers(6)) {
                for (EnumFacing facing : EnumFacing.HORIZONTALS) {
                    if (holeMineMode == HoleMineMode.BEST_EFFORT && processAntiSurround(player, facing)) return;
                }
            }
        }
        for (EntityPlayer player : EntityUtils.getNearbyPlayers(6)) {
            for (EnumFacing facing : EnumFacing.HORIZONTALS) {
                if (processHoleMine(player, facing)) return;
            }
        }
    }

    @EventHandler(timing = EventTiming.POST)
    public void onWorldRender(WorldRenderEvent e) {
        if (breakingBlock == null) return;
        AxisAlignedBB box = getWorld().getBlockState(breakingBlock).getBoundingBox(getWorld(), breakingBlock)
                .offset(breakingBlock).grow(0.002);
        RenderUtils.drawFilled(box, new Color(255, 0, 0, 50));
    }

    @Override
    public String getDefaultName() {
        return "AntiSurround";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.COMBAT;
    }
}
