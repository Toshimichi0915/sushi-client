package net.toshimichi.sushi.modules.combat;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.RootConfigurations;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.tick.ClientTickEvent;
import net.toshimichi.sushi.modules.*;
import net.toshimichi.sushi.task.forge.TaskExecutor;
import net.toshimichi.sushi.task.tasks.ItemSwitchTask;
import net.toshimichi.sushi.utils.combat.CivBreakAttack;
import net.toshimichi.sushi.utils.combat.CivBreakUtils;
import net.toshimichi.sushi.utils.player.InventoryUtils;
import net.toshimichi.sushi.utils.world.BlockPlaceInfo;
import net.toshimichi.sushi.utils.world.BlockUtils;

import java.util.Collections;
import java.util.List;

public class CivBreakModule extends BaseModule {

    private Configuration<Boolean> antiWeakness;
    private BlockPos breakingBlock;

    public CivBreakModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        antiWeakness = provider.get("anti_weakness", "Anti Weakness", null, Boolean.class, true);
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
    }

    private void placeCrystal(CivBreakAttack attack) {
        getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(attack.getObsidianPos(), EnumFacing.DOWN, EnumHand.MAIN_HAND,
                0.5F, 0, 0.5F));
    }

    @EventHandler(timing = EventTiming.POST)
    public void onClientTick(ClientTickEvent e) {
        List<CivBreakAttack> attacks = CivBreakUtils.find(getPlayer());
        if (attacks.isEmpty()) return;
        Collections.sort(attacks);
        CivBreakAttack attack = attacks.get(0);
        if (attack.isCrystalPlaced() && !attack.isObsidianPlaced()) {
            InventoryUtils.antiWeakness(antiWeakness.getValue(), () -> getConnection().sendPacket(new CPacketUseEntity(attack.getCrystal())));
        } else if (!attack.isObsidianPlaced()) {
            BlockPlaceInfo info = BlockUtils.findBlockPlaceInfo(getWorld(), attack.getObsidianPos());
            if (info == null) return;
            TaskExecutor.newTaskChain()
                    .supply(() -> Item.getItemFromBlock(Blocks.OBSIDIAN))
                    .then(new ItemSwitchTask(null, true))
                    .abortIfFalse()
                    .then(() -> BlockUtils.place(info, false))
                    .supply(() -> Items.END_CRYSTAL)
                    .then(new ItemSwitchTask(null, true))
                    .abortIfFalse()
                    .then(() -> placeCrystal(attack))
                    .execute();
            BlockUtils.place(info, false);
        } else if (!attack.isCrystalPlaced()) {
            TaskExecutor.newTaskChain()
                    .supply(() -> Items.END_CRYSTAL)
                    .then(new ItemSwitchTask(null, true))
                    .abortIfFalse()
                    .then(() -> placeCrystal(attack))
                    .execute();
        } else {
            if (!attack.getObsidianPos().equals(breakingBlock)) {
                breakingBlock = attack.getObsidianPos();
            }
            if (breakingBlock != BlockUtils.getBreakingBlockPos()) {
                TaskExecutor.newTaskChain()
                        .supply(() -> Items.DIAMOND_PICKAXE)
                        .then(new ItemSwitchTask(null, true))
                        .then(() -> getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, breakingBlock, EnumFacing.DOWN)))
                        .execute();
            } else {
                TaskExecutor.newTaskChain()
                        .supply(() -> Items.DIAMOND_PICKAXE)
                        .then(new ItemSwitchTask(null, true))
                        .then(() -> getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, breakingBlock, EnumFacing.DOWN)))
                        .execute();
            }
        }
    }

    public BlockPos getBreakingBlock() {
        return breakingBlock;
    }

    @Override
    public String getDefaultName() {
        return "CivBreak";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.COMBAT;
    }
}
