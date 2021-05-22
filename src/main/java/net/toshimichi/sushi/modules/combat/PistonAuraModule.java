package net.toshimichi.sushi.modules.combat;

import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.Configurations;
import net.toshimichi.sushi.config.data.IntRange;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.tick.ClientTickEvent;
import net.toshimichi.sushi.modules.*;
import net.toshimichi.sushi.task.forge.TaskExecutor;
import net.toshimichi.sushi.task.tasks.ItemSwitchMode;
import net.toshimichi.sushi.task.tasks.ItemSwitchTask;
import net.toshimichi.sushi.utils.*;

import java.util.Collections;
import java.util.List;

public class PistonAuraModule extends BaseModule {

    private final Configuration<IntRange> delay1;
    private final Configuration<IntRange> delay2;
    private final Configuration<IntRange> delay3;
    private final Configuration<IntRange> delay4;
    private final Configuration<IntRange> delay5;
    private boolean running;
    private int repeatCounter;

    public PistonAuraModule(String id, Modules modules, Categories categories, Configurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        delay1 = provider.get("delay_1", "Delay 1", null, IntRange.class, new IntRange(1, 20, 0, 1));
        delay2 = provider.get("delay_2", "Delay 2", null, IntRange.class, new IntRange(1, 20, 0, 1));
        delay3 = provider.get("delay_3", "Delay 3", null, IntRange.class, new IntRange(1, 20, 0, 1));
        delay4 = provider.get("delay_4", "Delay 4", null, IntRange.class, new IntRange(1, 20, 0, 1));
        delay5 = provider.get("delay_5", "Delay 5", null, IntRange.class, new IntRange(1, 20, 0, 1));
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

    @EventHandler(timing = EventTiming.PRE)
    public void onClientTick(ClientTickEvent e) {
        if (running) return;
        if (repeatCounter >= 4) {
            repeatCounter = 0;
            return;
        }
        List<PistonAuraAttack> attacks = PistonAuraUtils.find(getPlayer());
        if (attacks.isEmpty()) return;
        Collections.sort(attacks);
        PistonAuraAttack attack = attacks.get(0);
        if (attack.getPlaced() == null) {
            running = true;
            TaskExecutor.newTaskChain()
                    .supply(() -> Item.getItemById(426))
                    .then(new ItemSwitchTask(null, ItemSwitchMode.INVENTORY))
                    .abortIf(found -> {
                        if (!found) running = false;
                        return !found;
                    })
                    .then(() -> {
                        PositionUtils.desync(DesyncMode.LOOK);
                        PositionUtils.lookAt(BlockUtils.toVec3d(attack.getCrystalPos()), DesyncMode.LOOK);
                    }).delay(delay5.getValue().getCurrent())
                    .then(() -> {
                        getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(attack.getCrystalPos().add(0, -1, 0),
                                EnumFacing.DOWN, EnumHand.MAIN_HAND, 0.5F, 0, 0.5F));
                        PositionUtils.pop();
                    })
                    .then(() -> {
                        running = false;
                        if (delay1.getValue().getCurrent() == 0) {
                            repeatCounter++;
                            onClientTick(e);
                        }
                    }).execute();
        } else if (attack.isBlocked() || attack.isPistonActivated()) {
            running = true;
            TaskExecutor.newTaskChain()
                    .delay(delay2.getValue().getCurrent())
                    .then(() -> {
                        PositionUtils.desync(DesyncMode.LOOK);
                        PositionUtils.lookAt(attack.getPlaced().getPositionVector(), DesyncMode.LOOK);
                        getConnection().sendPacket(new CPacketUseEntity(attack.getPlaced()));
                        PositionUtils.pop();
                    })
                    .then(() -> {
                        running = false;
                        if (delay2.getValue().getCurrent() == 0) {
                            repeatCounter++;
                            onClientTick(e);
                        }
                    }).execute();
        } else if (!attack.isPistonPlaced()) {
            running = true;
            BlockPlaceInfo info = BlockUtils.findBlockPlaceInfo(getWorld(), attack.getPistonPos());
            if (info != null) {
                Vec3d lookAt = getPlayer().getPositionVector().add(new Vec3d(attack.getFacing().getOpposite().getDirectionVec()));
                TaskExecutor.newTaskChain()
                        .supply(() -> Item.getItemById(33))
                        .then(new ItemSwitchTask(null, ItemSwitchMode.INVENTORY))
                        .abortIf(found -> {
                            if (!found) running = false;
                            return !found;
                        })
                        .delay(delay3.getValue().getCurrent())
                        .then(() -> {
                            PositionUtils.desync(DesyncMode.LOOK);
                            PositionUtils.lookAt(lookAt, DesyncMode.LOOK);
                            BlockUtils.place(info);
                            PositionUtils.pop();
                        })
                        .then(() -> {
                            running = false;
                            if (delay3.getValue().getCurrent() == 0) {
                                repeatCounter++;
                                onClientTick(e);
                            }
                        }).execute();
            }
        } else if (!attack.isRedstonePlaced()) {
            running = true;
            for (EnumFacing facing : EnumFacing.values()) {
                BlockPlaceInfo info = BlockUtils.findBlockPlaceInfo(getWorld(), attack.getPistonPos().offset(facing));
                if (info != null) {
                    PositionUtils.lookAt(info, DesyncMode.LOOK);
                    TaskExecutor.newTaskChain()
                            .supply(() -> Item.getItemById(152))
                            .then(new ItemSwitchTask(null, ItemSwitchMode.INVENTORY))
                            .abortIf(found -> {
                                if (!found) running = false;
                                return !found;
                            })
                            .then(() -> BlockUtils.place(info))
                            .then(() -> {
                                running = false;
                                if (delay4.getValue().getCurrent() == 0) {
                                    repeatCounter++;
                                    onClientTick(e);
                                }
                            }).execute();
                    break;
                }
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
