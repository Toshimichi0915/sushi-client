package net.toshimichi.sushi.modules.combat;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.math.BlockPos;
import net.toshimichi.sushi.config.Config;
import net.toshimichi.sushi.config.ConfigInjector;
import net.toshimichi.sushi.config.RootConfigurations;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.tick.ClientTickEvent;
import net.toshimichi.sushi.modules.*;
import net.toshimichi.sushi.task.forge.TaskExecutor;
import net.toshimichi.sushi.task.tasks.BlockPlaceTask;
import net.toshimichi.sushi.task.tasks.ItemSwitchTask;
import net.toshimichi.sushi.utils.combat.DamageUtils;
import net.toshimichi.sushi.utils.player.DesyncCloseable;
import net.toshimichi.sushi.utils.player.DesyncMode;
import net.toshimichi.sushi.utils.player.PositionUtils;
import net.toshimichi.sushi.utils.world.BlockPlaceInfo;
import net.toshimichi.sushi.utils.world.BlockPlaceOption;
import net.toshimichi.sushi.utils.world.BlockUtils;

import java.util.Collections;

public class AntiCivBreakModule extends BaseModule {

    @Config(id = "extra_safe", name = "Extra Safe")
    public Boolean extraSafe = false;

    public AntiCivBreakModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
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
    }

    @EventHandler(timing = EventTiming.POST)
    public void onClientTick(ClientTickEvent e) {
        for (Entity entity : getWorld().loadedEntityList) {
            if (!(entity instanceof EntityEnderCrystal)) continue;
            if (entity.posY < getPlayer().posY + 2) continue;
            double posX = getPlayer().posX, posY = getPlayer().posY + getPlayer().height, posZ = getPlayer().posZ;
            if (entity.getDistanceSq(posX, posY, posZ) > 4) continue;
            BlockPos floorPos = BlockUtils.toBlockPos(entity.getPositionVector()).add(0, -1, 0);
            IBlockState floorState = getWorld().getBlockState(floorPos);
            Block floor = floorState.getBlock();
            if (floor != Blocks.OBSIDIAN) return;
            getWorld().setBlockState(floorPos, Blocks.AIR.getDefaultState());
            double damage = DamageUtils.getCrystalDamage(getPlayer(), entity.getPositionVector());
            getWorld().setBlockState(floorPos, floorState);
            if (damage <= 30) return;
            try (DesyncCloseable closeable = PositionUtils.desync(DesyncMode.POSITION)) {
                PositionUtils.move(posX, getPlayer().posY + 0.2, posZ, 0, 0, true, false, DesyncMode.POSITION);
            }
            getConnection().sendPacket(new CPacketUseEntity(entity));
            BlockPlaceInfo face = BlockUtils.findBlockPlaceInfo(getWorld(), BlockUtils.toBlockPos(entity.getPositionVector()),
                    new BlockPlaceOption(true, false));
            if (face == null) continue;
            TaskExecutor.newTaskChain()
                    .delay(1)
                    .supply(() -> Item.getItemFromBlock(Blocks.OBSIDIAN))
                    .then(new ItemSwitchTask(null, false))
                    .abortIfFalse()
                    .supply(() -> Collections.singletonList(face))
                    .then(new BlockPlaceTask(true, true, new BlockPlaceOption(true, false)))
                    .execute();
        }
    }

    @EventHandler(timing = EventTiming.POST)
    public void onClientTick2(ClientTickEvent e) {
        if (!extraSafe) return;
        BlockPos headPos = BlockUtils.toBlockPos(getPlayer().getPositionVector()).add(0, 2, 0);
        if (getWorld().getBlockState(headPos).getBlock() != Blocks.OBSIDIAN) return;
        BlockPos targetPos = headPos.add(0, 1, 0);
        BlockPlaceInfo info = BlockUtils.findBlockPlaceInfo(getWorld(), targetPos);
        if (info == null) return;
        TaskExecutor.newTaskChain()
                .supply(() -> Item.getItemFromBlock(Blocks.OBSIDIAN))
                .then(new ItemSwitchTask(null, true))
                .abortIfFalse()
                .supply(() -> Collections.singletonList(info))
                .then(new BlockPlaceTask(true, true))
                .execute();
    }

    @Override
    public String getDefaultName() {
        return "AntiCivBreak";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.COMBAT;
    }
}
