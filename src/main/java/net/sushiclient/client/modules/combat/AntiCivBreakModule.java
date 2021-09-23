package net.sushiclient.client.modules.combat;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.math.BlockPos;
import net.sushiclient.client.config.Config;
import net.sushiclient.client.config.ConfigInjector;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.config.data.IntRange;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.tick.ClientTickEvent;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.task.forge.TaskExecutor;
import net.sushiclient.client.task.tasks.BlockPlaceTask;
import net.sushiclient.client.utils.combat.DamageUtils;
import net.sushiclient.client.utils.player.*;
import net.sushiclient.client.utils.world.BlockPlaceInfo;
import net.sushiclient.client.utils.world.BlockUtils;
import net.sushiclient.client.utils.world.PlaceOptions;

import java.util.Collections;

public class AntiCivBreakModule extends BaseModule {

    @Config(id = "extra_safe", name = "Extra Safe")
    public Boolean extraSafe = false;

    @Config(id = "silent_switch", name = "Silent Switch")
    public Boolean silentSwitch = true;

    @Config(id = "damage", name = "Damage")
    public IntRange damage = new IntRange(30, 100, 10, 1);


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

    private void placeObsidian(BlockPlaceInfo info) {
        ItemSlot obsidian = InventoryUtils.findItemSlot(Item.getItemFromBlock(Blocks.OBSIDIAN), null, InventoryType.values());
        if (obsidian == null) return;
        obsidian = InventoryUtils.moveToHotbar(obsidian);
        InventoryUtils.silentSwitch(silentSwitch, obsidian.getIndex(), () -> {
            TaskExecutor.newTaskChain()
                    .supply(Collections.singletonList(info))
                    .then(new BlockPlaceTask(false, true, true, true, PlaceOptions.IGNORE_ENTITY))
                    .execute();
        });
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
            if (floor != Blocks.OBSIDIAN) continue;
            getWorld().setBlockState(floorPos, Blocks.AIR.getDefaultState());
            double damage = DamageUtils.getCrystalDamage(getPlayer(), entity.getPositionVector());
            getWorld().setBlockState(floorPos, floorState);
            if (damage <= this.damage.getCurrent()) continue;
            PositionUtils.require()
                    .desyncMode(DesyncMode.POSITION_LOOK)
                    .pos(posX, getPlayer().posY + 0.2, posZ)
                    .lookAt(entity.getPositionVector());
            BlockPlaceInfo face = BlockUtils.findBlockPlaceInfo(getWorld(), BlockUtils.toBlockPos(entity.getPositionVector()),
                    PlaceOptions.IGNORE_ENTITY);
            if (face == null) continue;
            PositionUtils.on(() -> {
                getConnection().sendPacket(new CPacketUseEntity(entity));
                TaskExecutor.newTaskChain()
                        .delay(1)
                        .then(() -> placeObsidian(face))
                        .execute();
            });
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
        placeObsidian(info);
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
