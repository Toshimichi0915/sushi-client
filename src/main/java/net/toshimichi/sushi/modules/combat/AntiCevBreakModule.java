package net.toshimichi.sushi.modules.combat;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.toshimichi.sushi.config.Configurations;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.tick.ClientTickEvent;
import net.toshimichi.sushi.modules.*;
import net.toshimichi.sushi.utils.*;

import java.util.List;

public class AntiCevBreakModule extends BaseModule {

    public AntiCevBreakModule(String id, Modules modules, Categories categories, Configurations provider, ModuleFactory factory) {
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
        List<Integer> hotbar = InventoryUtils.findItemFromHotbar(obsidian);
        if (hotbar.isEmpty()) return;
        for (Entity entity : getWorld().loadedEntityList) {
            if (!(entity instanceof EntityEnderCrystal)) continue;
            if (entity.posY < getPlayer().posY + 2) continue;
            double posX = getPlayer().posX, posY = getPlayer().posY + getPlayer().height, posZ = getPlayer().posZ;
            if (entity.getDistanceSq(posX, posY, posZ) > 4) continue;
            Block floor = getWorld().getBlockState(entity.getPosition().add(0, -1, 0)).getBlock();
            if (floor != Block.getBlockFromItem(obsidian)) return;
            getConnection().sendPacket(new CPacketUseEntity(entity));
            BlockFace face = BlockUtils.findFace(getWorld(), entity.getPosition());
            if (face == null) continue;
            getPlayer().inventory.currentItem = hotbar.get(0);
            PositionUtils.setSyncMode(SyncMode.NONE);
            PositionUtils.move(posX, getPlayer().posY + 0.2, posZ, 0, 0, true, false);
            PositionUtils.lookAt(face.getPos());
            PositionUtils.setSyncMode(SyncMode.BOTH);
            getController().updateController();
            getController().processRightClickBlock(getPlayer(), getWorld(), entity.getPosition(), face.getFacing(), face.getPos(), EnumHand.MAIN_HAND);
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
