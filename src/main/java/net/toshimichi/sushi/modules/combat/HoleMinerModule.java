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
import net.toshimichi.sushi.Sushi;
import net.toshimichi.sushi.config.Config;
import net.toshimichi.sushi.config.ConfigInjector;
import net.toshimichi.sushi.config.RootConfigurations;
import net.toshimichi.sushi.config.data.IntRange;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.tick.ClientTickEvent;
import net.toshimichi.sushi.modules.*;
import net.toshimichi.sushi.task.Task;
import net.toshimichi.sushi.task.forge.TaskExecutor;
import net.toshimichi.sushi.task.tasks.ItemSwitchTask;
import net.toshimichi.sushi.utils.EntityUtils;
import net.toshimichi.sushi.utils.TickUtils;
import net.toshimichi.sushi.utils.player.InventoryUtils;
import net.toshimichi.sushi.utils.player.ItemSlot;
import net.toshimichi.sushi.utils.player.ItemUtils;
import net.toshimichi.sushi.utils.render.hole.HoleUtils;
import net.toshimichi.sushi.utils.world.BlockUtils;

public class HoleMinerModule extends BaseModule {

    private boolean running;
    private HoleMineInfo holeMineInfo;
    private int lastUpdate;
    private int miningTick;

    @Config(id = "hole_mine_mode", name = "Mode")
    public HoleMineMode holeMineMode = HoleMineMode.BEST_EFFORT;

    @Config(id = "enable_crystal_aura", name = "Enable CrystalAura")
    public Boolean enableCrystalAura = true;

    @Config(id = "crystal_aura_id", name = "CrystalAura ID")
    public String crystalAuraId = "crystal_aura";

    @Config(id = "pause_ticks", name = "Pause Ticks")
    public IntRange pauseTick = new IntRange(1, 20, 1, 1);

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
        holeMineInfo = null;
    }

    private void start(HoleMineInfo info) {
        BlockPos surroundPos = info.getSurroundPos();
        // find diamond pickaxe
        ItemSlot pickaxe = InventoryUtils.findBestTool(true, false, Blocks.OBSIDIAN.getDefaultState());
        if (pickaxe.getItemStack().getItem() != Items.DIAMOND_PICKAXE) return;

        running = true;
        holeMineInfo = info;
        int waitTime = ItemUtils.getDestroyTime(surroundPos, pickaxe.getItemStack());
        if (!surroundPos.equals(BlockUtils.getBreakingBlockPos())) {
            getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, surroundPos, EnumFacing.DOWN));
            waitTime -= TickUtils.current() - BlockUtils.getBreakingTime();
        } else {
            waitTime = 0;
        }

        Task finishTask = () -> {
            getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, surroundPos, EnumFacing.DOWN));
            if (!enableCrystalAura) return;
            Module crystalAura = Sushi.getProfile().getModules().getModule(crystalAuraId);
            if (crystalAura == null) return;
            crystalAura.setEnabled(true);
        };

        Task lastTask = () -> {
            running = false;
            miningTick = TickUtils.current();
            holeMineInfo = null;
            if (disableAfter) setEnabled(false);
        };

        int finalWaitTime = waitTime;
        if (info.isAntiSurround()) {
            BlockPos crystalFloor = info.getCrystalFloor();
            TaskExecutor.newTaskChain()
                    .delay(finalWaitTime)
                    .abortIf(() -> !running)

                    .supply(() -> Items.END_CRYSTAL)
                    .then(new ItemSwitchTask(null, true))
                    .abortIfFalse()
                    .then(() -> getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(crystalFloor, EnumFacing.DOWN, EnumHand.MAIN_HAND, 0.5F, 0, 0.5F)))

                    .supply(() -> Items.DIAMOND_PICKAXE)
                    .then(new ItemSwitchTask(null, true))
                    .abortIfFalse()
                    .then(finishTask)
                    .last(lastTask)
                    .execute();
        } else {
            TaskExecutor.newTaskChain()
                    .delay(finalWaitTime)
                    .abortIf(() -> !running)

                    .supply(() -> Items.DIAMOND_PICKAXE)
                    .then(new ItemSwitchTask(null, true))
                    .abortIfFalse()
                    .then(finishTask)
                    .last(lastTask)
                    .execute();
        }
    }

    private HoleMineInfo choose(HoleMineInfo info1, HoleMineInfo info2) {
        if (info1 == null) return info2;
        if (info2 == null) return info1;
        if (info1.getSurroundPos().equals(BlockUtils.getBreakingBlockPos())) return info1;
        if (info2.getSurroundPos().equals(BlockUtils.getBreakingBlockPos())) return info2;
        int comp = Boolean.compare(info1.isAntiSurround(), info2.isAntiSurround());
        if (comp == 0) comp = Double.compare(
                BlockUtils.toVec3d(info1.getSurroundPos()).add(0.5, 0, 0.5).squareDistanceTo(getPlayer().getPositionVector()),
                BlockUtils.toVec3d(info2.getSurroundPos()).add(0.5, 0, 0.5).squareDistanceTo(getPlayer().getPositionVector()));
        return comp < 0 ? info1 : info2;
    }

    public void updateHoleMineInfo() {
        if (lastUpdate == TickUtils.current()) return;
        if (running) return;
        if (miningTick + pauseTick.getCurrent() > TickUtils.current() &&
                BlockUtils.getBreakingBlockPos() != null &&
                BlockUtils.isAir(getWorld(), BlockUtils.getBreakingBlockPos())) return;
        lastUpdate = TickUtils.current();
        if (!InventoryUtils.hasItem(Items.DIAMOND_PICKAXE)) return;
        HoleMineInfo chosen = null;
        for (EntityPlayer player : EntityUtils.getNearbyPlayers(6)) {
            for (EnumFacing facing : EnumFacing.HORIZONTALS) {
                if (InventoryUtils.hasItem(Items.END_CRYSTAL) && holeMineMode == HoleMineMode.BEST_EFFORT) {
                    chosen = choose(chosen, HoleUtils.findAntiSurround(player, facing));
                }
                chosen = choose(chosen, HoleUtils.findNormal(player, facing));
            }
        }
        this.holeMineInfo = chosen;
    }

    @EventHandler(timing = EventTiming.POST)
    public void onClientTick(ClientTickEvent e) {
        if (running) return;
        updateHoleMineInfo();
        if (holeMineInfo != null) start(holeMineInfo);
    }

    public HoleMineInfo getHoleMineInfo() {
        return holeMineInfo;
    }

    @Override
    public String getDefaultName() {
        return "HoleMiner";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.COMBAT;
    }

}
