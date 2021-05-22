package net.toshimichi.sushi.modules.player;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.math.AxisAlignedBB;
import net.toshimichi.sushi.config.Configurations;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.player.PlayerMoveEvent;
import net.toshimichi.sushi.modules.*;

import java.util.List;

public class AntiStuckModule extends BaseModule {

    private static final double SIZE = 0.03;

    public AntiStuckModule(String id, Modules modules, Categories categories, Configurations provider, ModuleFactory factory) {
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
    public void onPlayerMove(PlayerMoveEvent e) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        AxisAlignedBB box = player.getEntityBoundingBox();
        AxisAlignedBB ceil = box.grow(0, -SIZE, 0).offset(0, SIZE, 0);
        AxisAlignedBB floor = box.grow(0, -player.height / 2 + SIZE, 0).offset(0, -player.height / 2 + SIZE, 0);
        List<AxisAlignedBB> ceilBlocks = player.world.getCollisionBoxes(null, ceil);
        List<AxisAlignedBB> floorBlocks = player.world.getCollisionBoxes(null, floor);
        if (!ceilBlocks.isEmpty() || floorBlocks.isEmpty()) return;
        double maxY = 0;
        for (AxisAlignedBB block : floorBlocks) {
            if (maxY < block.maxY) maxY = block.maxY;
        }
        player.setPosition(player.posX, maxY + 0.1, player.posZ);
    }

    @Override
    public String getDefaultName() {
        return "AntiStuck";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.PLAYER;
    }
}
