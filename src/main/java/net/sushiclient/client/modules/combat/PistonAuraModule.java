package net.sushiclient.client.modules.combat;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.ConfigurationCategory;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.config.data.IntRange;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.tick.ClientTickEvent;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.task.forge.TaskExecutor;
import net.sushiclient.client.task.tasks.BlockPlaceTask;
import net.sushiclient.client.task.tasks.ItemSwitchMode;
import net.sushiclient.client.task.tasks.ItemSwitchTask;
import net.sushiclient.client.utils.UpdateTimer;
import net.sushiclient.client.utils.combat.PistonAuraAttack;
import net.sushiclient.client.utils.combat.PistonAuraUtils;
import net.sushiclient.client.utils.player.*;
import net.sushiclient.client.utils.world.BlockPlaceInfo;
import net.sushiclient.client.utils.world.BlockUtils;

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
    private final Configuration<IntRange> obsidianDelay;
    private final Configuration<IntRange> maxTargets;
    private final Configuration<Boolean> antiWeakness;
    private final Configuration<Boolean> disableOnDeath;
    private final UpdateTimer recalculationTimer;
    private final UpdateTimer obsidianTimer;
    private final UpdateTimer explosionTimer;
    private int timeout;

    private PistonAuraAttack attack;
    private EntityEnderCrystal exploded;
    private CloseablePositionOperator operator;
    private boolean running;
    private int repeatCounter;

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
        obsidianDelay = other.get("obsidian_delay", "Obsidian Delay", null, IntRange.class, new IntRange(20, 100, 1, 1));
        maxTargets = other.get("max_targets", "Max Targets", null, IntRange.class, new IntRange(1, 10, 1, 1));
        antiWeakness = other.get("anti_weakness", "Anti Weakness", null, Boolean.class, true);
        disableOnDeath = other.get("disable_on_death", "Disable On Death", null, Boolean.class, true);

        recalculationTimer = new UpdateTimer(false, recalculationDelay);
        obsidianTimer = new UpdateTimer(false, obsidianDelay);
        explosionTimer = new UpdateTimer(false, 10);
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
        timeout = 5;
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
        exploded = null;
        attack = null;
        PositionUtils.close(operator);
        operator = null;
        stop();
    }

    private void update(Configuration<IntRange> conf) {
        if (conf.getValue().getCurrent() == 0) {
            repeatCounter++;
            running = false;
            update();
        } else {
            repeatCounter = 0;
        }
    }

    private void stop() {
        running = false;
    }

    public void update() {
        if (running) return;
        if (repeatCounter >= 5) {
            repeatCounter = 0;
            return;
        }
        if (attack == null || repeatCounter == 0 && recalculationTimer.update()) {
            int obsidianCount = !obsidianTimer.peek() || repeatCounter != 0 ? 0 : maxObsidian.getValue().getCurrent();
            if (obsidianCount != 0) obsidianTimer.update();
            List<PistonAuraAttack> attacks = PistonAuraUtils.find(getPlayer(), obsidianCount, maxTargets.getValue().getCurrent());
            if (attacks.isEmpty()) return;
            Collections.sort(attacks);
            attack = attacks.get(0);
        }
        if (attack == null) return;
        if (exploded != null && !exploded.isDead && !explosionTimer.peek()) return;
        running = true;
        Vec3d lookAt = getPlayer().getPositionVector()
                .add(0, getPlayer().eyeHeight, 0)
                .add(new Vec3d(attack.getFacing().getOpposite().getDirectionVec()));
        if (operator == null) {
            PositionOperator fake = new PositionOperator();
            fake.desyncMode(PositionMask.LOOK).lookAt(lookAt);
            sendPacket(new CPacketPlayer.Rotation(fake.getYaw(), fake.getPitch(), getPlayer().onGround));
            operator = PositionUtils.desync().desyncMode(PositionMask.LOOK);
        }
        operator.lookAt(lookAt);
        final PistonAuraAttack attack = this.attack;
        if (!attack.isCrystalPlaced()) {
            TaskExecutor.newTaskChain()
                    .delay(delay1.getValue().getCurrent())
                    .supply(attack.getCrystalObsidian() == null ? null : Item.getItemFromBlock(Blocks.OBSIDIAN))
                    .then(new ItemSwitchTask(null, true))
                    .abortIfFalse()
                    .supply(attack::getCrystalObsidian)
                    .then(new BlockPlaceTask(true, true))
                    .delay(() -> attack.getCrystalObsidian() == null ? 0 : delay5.getValue().getCurrent())
                    .supply(Items.END_CRYSTAL)
                    .then(new ItemSwitchTask(null, ItemSwitchMode.INVENTORY))
                    .abortIfFalse()
                    .then(() -> {
                        sendPacket(new CPacketPlayerTryUseItemOnBlock(attack.getCrystalPos().add(0, -1, 0),
                                EnumFacing.DOWN, EnumHand.MAIN_HAND, 0.5F, 0, 0.5F));
                        attack.setCrystalPlaced(true);
                        update(delay1);
                    })
                    .last(this::stop)
                    .execute();
            return;
        } else if (attack.getCrystal() != null && (attack.getCrystal() == exploded || attack.isBlocked() || attack.isPistonActivated())) {
            TaskExecutor.newTaskChain()
                    .delay(delay2.getValue().getCurrent())
                    .then(() -> {
                        InventoryUtils.antiWeakness(antiWeakness.getValue(), () ->
                                sendPacket(new CPacketUseEntity(attack.getCrystal())));
                        exploded = attack.getCrystal();
                        explosionTimer.update();
                        this.attack = null;
                        update(delay2);
                    })
                    .last(this::stop)
                    .execute();
            return;
        } else if (!attack.isPistonPlaced()) {
            BlockPos pos = attack.getPistonPos();
            IBlockState state = getWorld().getBlockState(pos);
            TaskExecutor.newTaskChain()
                    .delay(delay3.getValue().getCurrent())
                    .supply(attack.getPistonObsidian() == null ? null : Item.getItemFromBlock(Blocks.OBSIDIAN))
                    .then(new ItemSwitchTask(null, true))
                    .abortIfFalse()
                    .supply(attack::getPistonObsidian)
                    .then(new BlockPlaceTask(false, false))
                    .delay(attack.getPistonObsidian() == null ? 0 : delay5.getValue().getCurrent())
                    .supply(Item.getItemFromBlock(Blocks.PISTON))
                    .then(new ItemSwitchTask(null, ItemSwitchMode.INVENTORY))
                    .abortIfFalse()
                    .then(() -> {
                        BlockPlaceInfo info = BlockUtils.findBlockPlaceInfo(getWorld(), attack.getPistonPos());
                        if (info == null) return;
                        BlockUtils.place(info, true);
                        getWorld().setBlockState(pos, Blocks.PISTON.getDefaultState());
                        attack.setPistonPlaced(true);
                        update(delay3);
                    })
                    .last(() -> getWorld().setBlockState(pos, state))
                    .last(this::stop)
                    .execute();
            return;
        } else if (!attack.isRedstonePlaced()) {
            boolean found = false;
            for (EnumFacing facing : new EnumFacing[]{EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.WEST, EnumFacing.UP}) {
                if (facing == attack.getFacing()) continue;
                BlockPos pos = attack.getPistonPos().offset(facing);
                IBlockState state = getWorld().getBlockState(pos);
                BlockPlaceInfo info = BlockUtils.findBlockPlaceInfo(getWorld(), pos);
                if (info == null) continue;
                TaskExecutor.newTaskChain()
                        .delay(delay4.getValue().getCurrent())
                        .supply(Item.getItemFromBlock(Blocks.REDSTONE_BLOCK))
                        .then(new ItemSwitchTask(null, ItemSwitchMode.INVENTORY))
                        .abortIfFalse()
                        .then(() -> {
                            BlockUtils.place(info, true);
                            attack.setRedstonePlaced(true);
                            getWorld().setBlockState(pos, Blocks.REDSTONE_BLOCK.getDefaultState());
                            update(delay4);
                        })
                        .last(() -> getWorld().setBlockState(pos, state))
                        .last(this::stop)
                        .execute();
                found = true;
                break;
            }
            if (found) return;
        }
        stop();
        this.attack = null;
        repeatCounter = 0;
    }

    @EventHandler(timing = EventTiming.POST)
    public void onPostClientTick(ClientTickEvent e) {
        if (disableOnDeath.getValue() && !getPlayer().isEntityAlive()) {
            setEnabled(false);
            return;
        }
        repeatCounter = 0;
        update();
        if (attack == null && timeout-- < 0) {
            PositionUtils.close(operator);
            operator = null;
        }
        if (attack != null) {
            timeout = 5;
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
