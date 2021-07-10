package net.toshimichi.sushi.modules.movement;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.RootConfigurations;
import net.toshimichi.sushi.config.data.IntRange;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.player.PlayerMoveEvent;
import net.toshimichi.sushi.modules.*;
import net.toshimichi.sushi.utils.player.DesyncMode;
import net.toshimichi.sushi.utils.player.PositionUtils;
import net.toshimichi.sushi.utils.world.BlockUtils;

public class StepModule extends BaseModule {

    private final Configuration<IntRange> height;
    private double motionX;
    private double motionZ;

    public StepModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        height = provider.get("height", "Height", null, IntRange.class, new IntRange(2, 8, 1, 1));
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
    public void onPrePlayerMove(PlayerMoveEvent e) {
        motionX = getPlayer().motionX;
        motionZ = getPlayer().motionZ;
    }

    private double getMaxHeight(AxisAlignedBB box) {
        Vec3d[] arr = {
                new Vec3d(box.minX, box.minY - 1, box.minZ),
                new Vec3d(box.maxX, box.minY - 1, box.minZ),
                new Vec3d(box.minX, box.minY - 1, box.maxZ),
                new Vec3d(box.maxX, box.minY - 1, box.maxZ)
        };
        double max = 0;
        for (Vec3d vec : arr) {
            BlockPos pos = BlockUtils.toBlockPos(vec);
            if (BlockUtils.isAir(getWorld(), pos)) continue;
            double y = getWorld().getBlockState(pos).getBoundingBox(getWorld(), pos).maxY + pos.getY();
            if (y > max) max = y;
        }
        return max;
    }

    @EventHandler(timing = EventTiming.POST)
    public void onPostPlayerMove(PlayerMoveEvent e) {
        for (int delta = 0; delta <= height.getValue().getCurrent(); delta++) {
            Vec3d posDelta = new Vec3d(motionX, 0, motionZ).normalize().scale(0.01).add(0, delta, 0);
            AxisAlignedBB box = getPlayer().getEntityBoundingBox().offset(posDelta);
            if (getWorld().collidesWithAnyBlock(box)) continue;
            if (delta == 0) return;
            Vec3d resultPos = getPlayer().getPositionVector().add(posDelta);
            PositionUtils.move(resultPos.x, getMaxHeight(box), resultPos.z, 0, 0, true, false, DesyncMode.NONE);
            getPlayer().motionX = motionX;
            getPlayer().motionY = 0;
            getPlayer().motionZ = motionZ;
            return;
        }
    }

    @Override
    public String getDefaultName() {
        return "Step";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.MOVEMENT;
    }
}
