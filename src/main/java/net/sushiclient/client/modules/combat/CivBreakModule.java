package net.sushiclient.client.modules.combat;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.config.data.IntRange;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.tick.GameTickEvent;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.task.forge.TaskExecutor;
import net.sushiclient.client.task.tasks.ItemSlotSwitchTask;
import net.sushiclient.client.task.tasks.ItemSwitchTask;
import net.sushiclient.client.utils.TickUtils;
import net.sushiclient.client.utils.UpdateTimer;
import net.sushiclient.client.utils.combat.CivBreakAttack;
import net.sushiclient.client.utils.combat.CivBreakUtils;
import net.sushiclient.client.utils.player.InventoryUtils;
import net.sushiclient.client.utils.player.ItemSlot;
import net.sushiclient.client.utils.player.ItemUtils;
import net.sushiclient.client.utils.world.BlockPlaceInfo;
import net.sushiclient.client.utils.world.BlockUtils;

import java.util.Collections;
import java.util.List;

public class CivBreakModule extends BaseModule {

    private final Configuration<Boolean> antiWeakness;
    private final Configuration<IntRange> damage;
    private final Configuration<IntRange> selfDamage;
    private final UpdateTimer breakTimer;
    private BlockPos breakingBlock;

    public CivBreakModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        antiWeakness = provider.get("anti_weakness", "Anti Weakness", null, Boolean.class, true);
        damage = provider.get("damage", "Damage", null, IntRange.class, new IntRange(40, 100, 10, 1));
        selfDamage = provider.get("self_damage", "Self Damage", null, IntRange.class, new IntRange(40, 100, 10, 1));
        breakTimer = new UpdateTimer(false, 1);
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
        sendPacket(new CPacketPlayerTryUseItemOnBlock(attack.getObsidianPos(), EnumFacing.DOWN, EnumHand.MAIN_HAND,
                0.5F, 0, 0.5F));
    }

    @EventHandler(timing = EventTiming.POST)
    public void onGameTick(GameTickEvent e) {
        List<CivBreakAttack> attacks = CivBreakUtils.find(getPlayer(), damage.getValue().getCurrent(), selfDamage.getValue().getCurrent());
        if (attacks.isEmpty()) return;
        Collections.sort(attacks);
        CivBreakAttack attack = attacks.get(0);
        if (attack.isCrystalPlaced() && !attack.isObsidianPlaced()) {
            InventoryUtils.antiWeakness(antiWeakness.getValue(), () -> sendPacket(new CPacketUseEntity(attack.getCrystal())));
        } else if (!attack.isObsidianPlaced()) {
            BlockPlaceInfo info = BlockUtils.findBlockPlaceInfo(getWorld(), attack.getObsidianPos());
            if (info == null) return;
            TaskExecutor.newTaskChain()
                    .supply(Item.getItemFromBlock(Blocks.OBSIDIAN))
                    .then(new ItemSwitchTask(null, true))
                    .abortIfFalse()
                    .then(() -> BlockUtils.place(info, false))
                    .supply(Items.END_CRYSTAL)
                    .then(new ItemSwitchTask(null, true))
                    .abortIfFalse()
                    .then(() -> placeCrystal(attack))
                    .execute();
            BlockUtils.place(info, false);
        } else if (!attack.isCrystalPlaced()) {
            TaskExecutor.newTaskChain()
                    .supply(Items.END_CRYSTAL)
                    .then(new ItemSwitchTask(null, true))
                    .abortIfFalse()
                    .then(() -> placeCrystal(attack))
                    .execute();
        } else { // attack.isCrystalPlaced() && attack.isObsidianPlaced()
            if (!attack.getObsidianPos().equals(breakingBlock)) {
                breakingBlock = attack.getObsidianPos();
            }
            int waitTime = TickUtils.current() - BlockUtils.getBreakingTime();
            ItemSlot pickaxe = InventoryUtils.findBestTool(true, false, Blocks.OBSIDIAN.getDefaultState());
            if (pickaxe.getItemStack().getItem() != Items.DIAMOND_PICKAXE) return;
            if (breakingBlock != BlockUtils.getBreakingBlockPos()) {
                TaskExecutor.newTaskChain()
                        .supply(pickaxe)
                        .then(new ItemSlotSwitchTask())
                        .then(() -> sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, breakingBlock, EnumFacing.DOWN)))
                        .execute();
            } else if (waitTime > ItemUtils.getDestroyTime(attack.getObsidianPos(), pickaxe.getItemStack()) && breakTimer.update()) {
                TaskExecutor.newTaskChain()
                        .supply(pickaxe)
                        .then(new ItemSlotSwitchTask())
                        .then(() -> {
                            sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, breakingBlock, EnumFacing.DOWN));
                            InventoryUtils.antiWeakness(antiWeakness.getValue(), () -> sendPacket(new CPacketUseEntity(attack.getCrystal())));
                        })
                        .execute();
            }
        }
    }

    public int getEnemyDamage() {
        return damage.getValue().getCurrent();
    }

    public int getSelfDamage() {
        return selfDamage.getValue().getCurrent();
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
