package net.toshimichi.sushi.modules.world;

import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.toshimichi.sushi.config.RootConfigurations;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.tick.ClientTickEvent;
import net.toshimichi.sushi.modules.*;
import net.toshimichi.sushi.utils.EntityInfo;
import net.toshimichi.sushi.utils.EntityUtils;
import net.toshimichi.sushi.utils.world.BlockUtils;

public class AntiGhostBlockModule extends BaseModule {

    public AntiGhostBlockModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
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

    @EventHandler(timing = EventTiming.POST)
    public void onClientTick(ClientTickEvent e) {
        for (EntityInfo<EntityEnderCrystal> crystal : EntityUtils.getNearbyEntities(getPlayer().getPositionVector(), EntityEnderCrystal.class)) {
            BlockPos pos = BlockUtils.toBlockPos(crystal.getEntity().getPositionVector());
            if (BlockUtils.isAir(getWorld(), pos)) continue;
            if (!EntityUtils.canInteract(getPlayer().getPositionVector(), crystal.getEntity().getPositionVector(), 6, 3))
                return;
            getWorld().setBlockState(pos, Blocks.AIR.getDefaultState());
            BlockUtils.checkGhostBlock(pos);
        }
    }

    @Override
    public String getDefaultName() {
        return "AntiGhostBlock";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.WORLD;
    }
}
