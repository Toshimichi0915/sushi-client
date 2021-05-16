package net.toshimichi.sushi.modules.world;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.toshimichi.sushi.config.Configurations;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.tick.ClientTickEvent;
import net.toshimichi.sushi.modules.*;
import net.toshimichi.sushi.utils.BlockPlaceInfo;
import net.toshimichi.sushi.utils.BlockUtils;
import net.toshimichi.sushi.utils.DesyncMode;
import net.toshimichi.sushi.utils.PositionUtils;

public class ScaffoldModule extends BaseModule {

    public ScaffoldModule(String id, Modules modules, Categories categories, Configurations provider, ModuleFactory factory) {
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
        Vec3d floor = getPlayer().getPositionVector().add(0, -1, 0);
        BlockPos floorPos = new BlockPos((int) floor.x, (int) floor.y, (int) floor.z);
        BlockPlaceInfo info = BlockUtils.findFace(getWorld(), floorPos);
        if (info == null && BlockUtils.isAir(getWorld(), floorPos)) {
            for (EnumFacing facing : EnumFacing.values()) {
                BlockPlaceInfo sub = BlockUtils.findFace(getWorld(), floorPos.offset(facing));
                if (sub == null) continue;
                PositionUtils.desync(DesyncMode.LOOK);
                PositionUtils.lookAt(sub, DesyncMode.LOOK);
                BlockUtils.place(sub);
                PositionUtils.pop();
                break;
            }
        } else if (info != null) {
            PositionUtils.desync(DesyncMode.LOOK);
            PositionUtils.lookAt(info, DesyncMode.LOOK);
            BlockUtils.place(info);
            PositionUtils.pop();
        }
    }

    @Override
    public String getDefaultName() {
        return "Scaffold";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.WORLD;
    }
}
