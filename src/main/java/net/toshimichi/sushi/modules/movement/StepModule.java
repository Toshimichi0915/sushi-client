package net.toshimichi.sushi.modules.movement;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.RootConfigurations;
import net.toshimichi.sushi.config.data.DoubleRange;
import net.toshimichi.sushi.config.data.IntRange;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.player.PlayerMoveEvent;
import net.toshimichi.sushi.modules.*;
import net.toshimichi.sushi.utils.player.DesyncMode;
import net.toshimichi.sushi.utils.player.PositionUtils;
import net.toshimichi.sushi.utils.render.hole.HoleUtils;
import net.toshimichi.sushi.utils.world.BlockUtils;

public class StepModule extends BaseModule {

    private final Configuration<IntRange> height;
    private final Configuration<DoubleRange> delta;
    private final Configuration<Boolean> pauseInHole;
    private final Configuration<Boolean> pauseOnSneak;
    private double motionX;
    private double motionZ;

    public StepModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        height = provider.get("height", "Height", null, IntRange.class, new IntRange(2, 8, 1, 1));
        delta = provider.get("delta", "Delta", null, DoubleRange.class, new DoubleRange(0.1, 1, 0, 0.1, 1));
        pauseInHole = provider.get("pause_in_hole", "Pause In Hole", null, Boolean.class, true);
        pauseOnSneak = provider.get("pause_on_sneak", "Pause On Sneak", null, Boolean.class, true);
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
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


    @EventHandler(timing = EventTiming.PRE)
    public void onPrePlayerMove(PlayerMoveEvent e) {
        motionX = getPlayer().motionX;
        motionZ = getPlayer().motionZ;
    }

    @EventHandler(timing = EventTiming.POST)
    public void onPostPlayerMove(PlayerMoveEvent e) {
        BlockPos floorPos = BlockUtils.toBlockPos(getPlayer().getPositionVector());
        if (pauseInHole.getValue() && HoleUtils.getHoleInfo(getWorld(), floorPos) != null ||
                pauseOnSneak.getValue() && getPlayer().isSneaking()) {
            return;
        }
        Vec3d direction = new Vec3d(motionX, 0, motionZ).normalize();
        for (int y = 0; y <= height.getValue().getCurrent(); y++) {
            Vec3d pos = direction.scale(0.01).add(0, y, 0);
            Vec3d scaled = direction.scale(delta.getValue().getCurrent());
            AxisAlignedBB box = getPlayer().getEntityBoundingBox().offset(pos);
            AxisAlignedBB box2 = getPlayer().getEntityBoundingBox().offset(scaled).offset(pos);
            if (!getWorld().collidesWithAnyBlock(box) && y == 0) return;
            if (getWorld().collidesWithAnyBlock(box2)) continue;
            Vec3d resultPos = getPlayer().getPositionVector().add(scaled).add(pos);
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
