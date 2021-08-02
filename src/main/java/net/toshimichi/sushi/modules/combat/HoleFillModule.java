package net.toshimichi.sushi.modules.combat;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.toshimichi.sushi.Sushi;
import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.RootConfigurations;
import net.toshimichi.sushi.config.data.DoubleRange;
import net.toshimichi.sushi.config.data.IntRange;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.tick.ClientTickEvent;
import net.toshimichi.sushi.modules.*;
import net.toshimichi.sushi.modules.render.HoleEspModule;
import net.toshimichi.sushi.task.forge.TaskExecutor;
import net.toshimichi.sushi.task.tasks.BlockPlaceTask;
import net.toshimichi.sushi.task.tasks.ItemSwitchTask;
import net.toshimichi.sushi.utils.render.hole.HoleInfo;
import net.toshimichi.sushi.utils.world.BlockPlaceInfo;
import net.toshimichi.sushi.utils.world.BlockUtils;

import java.util.ArrayList;
import java.util.List;

public class HoleFillModule extends BaseModule {

    private static final Vec3d ZERO = new Vec3d(0, 0, 0);
    private final Configuration<String> holeEspModule;
    private final Configuration<Boolean> doubleHole;
    private final Configuration<IntRange> theta;
    private final Configuration<DoubleRange> distance;
    private boolean running;

    public HoleFillModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        holeEspModule = provider.get("hole_esp_module", "Hole ESP Module", null, String.class, "hole_esp");
        doubleHole = provider.get("double_hole", "Double Hole", null, Boolean.class, false);
        theta = provider.get("theta", "Theta", null, IntRange.class, new IntRange(10, 90, 0, 1));
        distance = provider.get("distance", "Distance", null, DoubleRange.class, new DoubleRange(1, 4, 0, 0.1, 1));
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
        if (running) return;
        Module module = Sushi.getProfile().getModules().getModule(holeEspModule.getValue());
        if (!(module instanceof HoleEspModule)) return;
        List<HoleInfo> holes = ((HoleEspModule) module).getHoles();
        ArrayList<BlockPlaceInfo> placeList = new ArrayList<>();
        for (HoleInfo hole : holes) {
            if (hole.getHoleType().isDouble() && !doubleHole.getValue()) continue;
            for (BlockPos pos : hole.getBlockPos()) {

                // distance check
                Vec3d blockVec = BlockUtils.toVec3d(pos).add(0.5, 0.5, 0.5);
                if (getPlayer().getPositionVector().squareDistanceTo(blockVec) < Math.pow(distance.getValue().getCurrent(), 2)) {
                    continue;
                }

                // rotation check
                Vec3d v1 = getPlayer().getLookVec();
                v1 = new Vec3d(v1.x, 0, v1.z);
                Vec3d v2 = new Vec3d(pos.getX() + 0.5 - getPlayer().getPositionVector().x, 0, pos.getZ() + 0.5 - getPlayer().getPositionVector().z);
                double cos = (v1.x * v2.x + v1.z * v2.z) / v1.distanceTo(ZERO) / v2.distanceTo(ZERO);

                if (cos > MathHelper.cos((float) Math.toRadians(theta.getValue().getCurrent()))) continue;
                BlockPlaceInfo info = BlockUtils.findBlockPlaceInfo(getWorld(), pos);
                if (info == null) continue;
                placeList.add(info);
            }
        }
        if (placeList.isEmpty()) return;

        running = true;
        TaskExecutor.newTaskChain()
                .supply(Item.getItemFromBlock(Blocks.OBSIDIAN))
                .then(new ItemSwitchTask(null, true))
                .abortIfFalse()
                .supply(placeList)
                .then(new BlockPlaceTask(true, true))
                .last(() -> running = false)
                .execute();
    }

    @Override
    public String getDefaultName() {
        return "HoleFill";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.COMBAT;
    }
}
