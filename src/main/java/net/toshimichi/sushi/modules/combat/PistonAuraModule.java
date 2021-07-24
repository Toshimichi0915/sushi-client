package net.toshimichi.sushi.modules.combat;

import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.ConfigurationCategory;
import net.toshimichi.sushi.config.RootConfigurations;
import net.toshimichi.sushi.config.data.IntRange;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.tick.ClientTickEvent;
import net.toshimichi.sushi.modules.*;
import net.toshimichi.sushi.task.forge.TaskExecutor;
import net.toshimichi.sushi.task.tasks.BlockPlaceTask;
import net.toshimichi.sushi.task.tasks.ItemSwitchMode;
import net.toshimichi.sushi.task.tasks.ItemSwitchTask;
import net.toshimichi.sushi.utils.TickUtils;
import net.toshimichi.sushi.utils.combat.PistonAuraAttack;
import net.toshimichi.sushi.utils.combat.PistonAuraUtils;
import net.toshimichi.sushi.utils.player.DesyncCloseable;
import net.toshimichi.sushi.utils.player.DesyncMode;
import net.toshimichi.sushi.utils.player.PositionUtils;
import net.toshimichi.sushi.utils.world.BlockPlaceInfo;
import net.toshimichi.sushi.utils.world.BlockUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PistonAuraModule extends BaseModule {
    private final Configuration<IntRange> delay1;
    private final Configuration<IntRange> delay2;
    private final Configuration<IntRange> delay3;
    private final Configuration<IntRange> delay4;
    private final Configuration<IntRange> delay5;
    private final Configuration<IntRange> maxObsidian;
    private final Configuration<IntRange> recalculationDelay;
    private final Configuration<Boolean> antiGhostBlock;
    private final Configuration<IntRange> ghostBlockCheckDelay;

    private final ArrayList<BlockPos> ghostBlocks = new ArrayList<>();
    private PistonAuraAttack attack;
    private EntityEnderCrystal exploded;
    private DesyncCloseable closeable;
    private boolean running;
    private int repeatCounter;
    private int lastRecalculationTick;
    private int lastGhostBlockCheckTick;

    public PistonAuraModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        ConfigurationCategory delay = provider.getCategory("delay", "Delay Settings", null);
        delay1 = delay.get("delay_1", "Crystal Place Delay", null, IntRange.class, new IntRange(0, 20, 0, 1));
        delay2 = delay.get("delay_2", "Crystal Break Delay", null, IntRange.class, new IntRange(1, 20, 0, 1));
        delay3 = delay.get("delay_3", "Piston Place Delay", null, IntRange.class, new IntRange(0, 20, 0, 1));
        delay4 = delay.get("delay_4", "Redstone Place Delay", null, IntRange.class, new IntRange(0, 20, 0, 1));
        delay5 = delay.get("delay_5", "Obsidian Place Delay", null, IntRange.class, new IntRange(1, 20, 0, 1));
        ConfigurationCategory other = provider.getCategory("other", "Other Settings", null);
        maxObsidian = other.get("max_obsidian", "Max Obsidian", null, IntRange.class, new IntRange(3, 5, 0, 1));
        recalculationDelay = other.get("recalculation_delay", "Recalculation Delay", null, IntRange.class, new IntRange(1, 40, 0, 1));
        antiGhostBlock = other.get("anti_ghost_block", "Anti Ghost Block", null, Boolean.class, true);
        ghostBlockCheckDelay = other.get("ghost_block_check_delay", "Ghost Block Check Delay", null, IntRange.class, new IntRange(1, 20, 0, 1), antiGhostBlock::getValue, false, 0);
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
        exploded = null;
        stop();
    }

    private void update(Configuration<IntRange> conf) {
        if (conf.getValue().getCurrent() == 0) {
            running = false;
            repeatCounter++;
            update();
        } else {
            repeatCounter = 0;
        }
    }

    private void stop() {
        running = false;
        if (closeable != null) {
            closeable.close();
            closeable = null;
        }
    }

    public void update() {
        if (running) return;
        if (repeatCounter >= 5) {
            repeatCounter = 0;
            return;
        }
        if (attack == null || repeatCounter == 0 && lastRecalculationTick-- <= 0) {
            List<PistonAuraAttack> attacks = PistonAuraUtils.find(getPlayer(), maxObsidian.getValue().getCurrent());
            if (attacks.isEmpty()) return;
            Collections.sort(attacks);
            attack = attacks.get(0);
            lastRecalculationTick = recalculationDelay.getValue().getCurrent();
        }
        if (attack == null) return;
        if (exploded != null && exploded.isAddedToWorld()) return;
        running = true;
        if (closeable == null) {
            closeable = PositionUtils.desync(DesyncMode.LOOK);
        }
        Vec3d lookAt = getPlayer().getPositionVector().add(new Vec3d(attack.getFacing().getOpposite().getDirectionVec()));
        PositionUtils.lookAt(lookAt, DesyncMode.LOOK);
        final PistonAuraAttack attack = this.attack;
        if (!attack.isCrystalPlaced()) {
            TaskExecutor.newTaskChain()
                    .delay(delay1.getValue().getCurrent())
                    .supply(() -> attack.getObsidianPlace() == null ? null : Item.getItemFromBlock(Blocks.OBSIDIAN))
                    .then(new ItemSwitchTask(null, true))
                    .abortIfFalse()
                    .supply(attack::getObsidianPlace)
                    .then(new BlockPlaceTask(true, true))
                    .delay(() -> attack.getObsidianPlace() == null ? 0 : delay5.getValue().getCurrent())
                    .supply(() -> Items.END_CRYSTAL)
                    .then(new ItemSwitchTask(null, ItemSwitchMode.INVENTORY))
                    .abortIfFalse()
                    .then(() -> {
                        ghostBlocks.add(attack.getPistonPos());
                        for (EnumFacing facing : EnumFacing.values()) {
                            BlockPos redstone = attack.getPistonPos().offset(facing);
                            if (getWorld().getBlockState(redstone).getBlock() == Blocks.REDSTONE_BLOCK) {
                                ghostBlocks.add(redstone);
                            }
                        }
                        getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(attack.getCrystalPos().add(0, -1, 0),
                                EnumFacing.DOWN, EnumHand.MAIN_HAND, 0.5F, 0, 0.5F));
                        attack.setCrystalPlaced(true);
                        update(delay1);
                    })
                    .last(this::stop)
                    .execute();
        } else if (attack.getCrystal() != null && (attack.getCrystal() == exploded || attack.isBlocked() || attack.isPistonActivated())) {
            TaskExecutor.newTaskChain()
                    .delay(delay2.getValue().getCurrent())
                    .then(() -> {
                        getConnection().sendPacket(new CPacketUseEntity(attack.getCrystal()));
                        getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(attack.getCrystalPos().add(0, -1, 0),
                                EnumFacing.DOWN, EnumHand.MAIN_HAND, 0.5F, 0, 0.5F));
                        exploded = attack.getCrystal();
                        this.attack = null;
                        update(delay2);
                    })
                    .last(this::stop)
                    .execute();
        } else if (!attack.isPistonPlaced()) {
            TaskExecutor.newTaskChain()
                    .delay(delay3.getValue().getCurrent())
                    .supply(() -> attack.getPistonPlace() == null ? null : Item.getItemFromBlock(Blocks.OBSIDIAN))
                    .then(new ItemSwitchTask(null, true))
                    .abortIfFalse()
                    .supply(attack::getPistonPlace)
                    .then(new BlockPlaceTask(false, false))
                    .delay(attack.getPistonPlace() == null ? 0 : delay5.getValue().getCurrent())
                    .supply(() -> Item.getItemFromBlock(Blocks.PISTON))
                    .then(new ItemSwitchTask(null, ItemSwitchMode.INVENTORY))
                    .abortIfFalse()
                    .then(() -> {
                        BlockPlaceInfo info = BlockUtils.findBlockPlaceInfo(getWorld(), attack.getPistonPos());
                        if (info == null) return;
                        BlockUtils.place(info);
                        attack.setPistonPlaced(true);
                        update(delay3);
                    })
                    .last(this::stop)
                    .execute();
        } else if (!attack.isRedstonePlaced()) {
            for (EnumFacing facing : EnumFacing.values()) {
                if (facing == attack.getFacing()) continue;
                BlockPlaceInfo info = BlockUtils.findBlockPlaceInfo(getWorld(), attack.getPistonPos().offset(facing));
                if (info == null) continue;
                TaskExecutor.newTaskChain()
                        .delay(delay4.getValue().getCurrent())
                        .supply(() -> Item.getItemFromBlock(Blocks.REDSTONE_BLOCK))
                        .then(new ItemSwitchTask(null, ItemSwitchMode.INVENTORY))
                        .abortIfFalse()
                        .then(() -> {
                            BlockUtils.place(info);
                            attack.setRedstonePlaced(true);
                            update(delay4);
                        })
                        .last(this::stop)
                        .execute();
                break;
            }
        } else {
            stop();
            this.attack = null;
            repeatCounter = 0;
        }
    }

    @EventHandler(timing = EventTiming.POST)
    public void onPostClientTick(ClientTickEvent e) {
        repeatCounter = 0;
        update();
        if (TickUtils.current() - lastGhostBlockCheckTick < ghostBlockCheckDelay.getValue().getCurrent()) return;
        lastGhostBlockCheckTick = TickUtils.current();
        while (true) {
            if (ghostBlocks.isEmpty()) break;
            BlockPos checkPos = ghostBlocks.get(0);
            ghostBlocks.removeIf(it -> BlockUtils.equals(checkPos, it));
            if (BlockUtils.canInteract(checkPos)) {
                BlockUtils.checkGhostBlock(checkPos);
                break;
            }
        }
    }

    @Override
    public String getDefaultName() {
        return "PistonAura";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.COMBAT;
    }
}
