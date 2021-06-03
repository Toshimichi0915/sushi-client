package net.toshimichi.sushi.modules.combat;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.toshimichi.sushi.config.RootConfigurations;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.tick.ClientTickEvent;
import net.toshimichi.sushi.modules.*;
import net.toshimichi.sushi.task.forge.TaskExecutor;
import net.toshimichi.sushi.task.tasks.BlockPlaceTask;
import net.toshimichi.sushi.task.tasks.ItemSwitchTask;
import net.toshimichi.sushi.utils.player.DesyncMode;
import net.toshimichi.sushi.utils.player.PositionUtils;
import net.toshimichi.sushi.utils.world.BlockPlaceInfo;
import net.toshimichi.sushi.utils.world.BlockUtils;

import java.util.Collections;

public class AntiCevBreakModule extends BaseModule {

    public AntiCevBreakModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onClientTick(ClientTickEvent e) {
        Item obsidian = Item.getItemById(49);
        for (Entity entity : getWorld().loadedEntityList) {
            if (!(entity instanceof EntityEnderCrystal)) continue;
            if (entity.posY < getPlayer().posY + 2) continue;
            double posX = getPlayer().posX, posY = getPlayer().posY + getPlayer().height, posZ = getPlayer().posZ;
            if (entity.getDistanceSq(posX, posY, posZ) > 4) continue;
            Block floor = getWorld().getBlockState(entity.getPosition().add(0, -1, 0)).getBlock();
            if (floor != Block.getBlockFromItem(obsidian)) return;
            PositionUtils.desync(DesyncMode.POSITION);
            PositionUtils.move(posX, getPlayer().posY + 0.2, posZ, 0, 0, true, false, DesyncMode.POSITION);
            PositionUtils.pop();
            getConnection().sendPacket(new CPacketUseEntity(entity));
            BlockPlaceInfo face = BlockUtils.findBlockPlaceInfo(getWorld(), entity.getPosition());
            if (face == null) continue;
            TaskExecutor.newTaskChain()
                    .supply(() -> obsidian)
                    .then(new ItemSwitchTask(null, false))
                    .abortIf(found -> !found)
                    .supply(() -> Collections.singletonList(face))
                    .then(new BlockPlaceTask(DesyncMode.LOOK))
                    .execute(true);
        }
    }

    @Override
    public String getDefaultName() {
        return "AntiCevBreak";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.COMBAT;
    }
}
