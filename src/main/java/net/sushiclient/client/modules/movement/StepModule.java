package net.sushiclient.client.modules.movement;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.config.data.DoubleRange;
import net.sushiclient.client.config.data.IntRange;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.player.PlayerMoveEvent;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.utils.EntityUtils;
import net.sushiclient.client.utils.player.DesyncMode;
import net.sushiclient.client.utils.player.PositionUtils;
import net.sushiclient.client.utils.render.hole.HoleUtils;
import net.sushiclient.client.utils.world.BlockUtils;

import java.util.ArrayList;
import java.util.List;

public class StepModule extends BaseModule {

    private final Configuration<Boolean> phase;
    private final Configuration<Boolean> normal;
    private final Configuration<IntRange> height;
    private final Configuration<DoubleRange> delta;

    private final Configuration<Boolean> reverse;
    private final Configuration<IntRange> reverseHeight;
    private final Configuration<DoubleRange> reverseMinHeight;

    private final Configuration<Boolean> pauseInHole;
    private final Configuration<Boolean> pauseOnSneak;
    private double motionX;
    private double motionZ;

    public StepModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        phase = provider.get("phase", "Phase", null, Boolean.class, true);
        normal = provider.get("normal", "Normal", null, Boolean.class, true);
        height = provider.get("height", "Height", null, IntRange.class, new IntRange(2, 8, 1, 1), normal::getValue, false, 0);
        delta = provider.get("delta", "Delta", null, DoubleRange.class, new DoubleRange(0.1, 1, 0, 0.1, 1), normal::getValue, false, 0);

        reverse = provider.get("reverse", "Reverse", null, Boolean.class, true);
        reverseHeight = provider.get("reverse_height", "Reverse Height", null, IntRange.class, new IntRange(2, 8, 1, 1), reverse::getValue, false, 0);
        reverseMinHeight = provider.get("reverse_min_height", "Reverse Min Height", null, DoubleRange.class, new DoubleRange(0.3, 1, 0, 0.1, 1), reverse::getValue, false, 0);

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
        List<AxisAlignedBB> collisions = getWorld().getCollisionBoxes(null, box.offset(0, -1, 0));
        boolean updated = false;
        double maxY = 0;
        for (AxisAlignedBB collision : collisions) {
            if (collision.maxY > maxY) {
                updated = true;
                maxY = collision.maxY;
            }

        }
        return updated ? maxY : Double.NaN;
    }

    private void sendAll(List<Vec3d> packets) {
        for (Vec3d vec : packets) {
            PositionUtils.move(vec.x, vec.y, vec.z, 0, 0, true, false, DesyncMode.NONE);
        }
    }

    @EventHandler(timing = EventTiming.PRE, priority = 50000)
    public void onPrePlayerMove(PlayerMoveEvent e) {
        motionX = getPlayer().motionX;
        motionZ = getPlayer().motionZ;
    }

    @EventHandler(timing = EventTiming.POST)
    public void onPostPlayerMove(PlayerMoveEvent e) {
        if (EntityUtils.isInsideBlock(getPlayer())) return;
        BlockPos floorPos = BlockUtils.toBlockPos(getPlayer().getPositionVector());
        if (pauseInHole.getValue() && HoleUtils.getHoleInfo(getWorld(), floorPos, false) != null ||
                pauseOnSneak.getValue() && getPlayer().isSneaking()) {
            return;
        }
        Vec3d direction = new Vec3d(motionX, 0, motionZ).normalize();
        Vec3d scaled = direction.scale(delta.getValue().getCurrent());
        if (reverse.getValue()) {
            for (int y = -reverseHeight.getValue().getCurrent(); y <= 0; y++) {
                Vec3d pos = direction.scale(0.01).add(0, y, 0);
                AxisAlignedBB box = getPlayer().getEntityBoundingBox().offset(pos);
                if (getWorld().collidesWithAnyBlock(box)) continue;
                Vec3d resultPos = getPlayer().getPositionVector().add(pos);
                double height = getMaxHeight(box);
                if (getPlayer().posY - height < reverseMinHeight.getValue().getCurrent()) continue;
                if (Double.isNaN(height)) continue;
                if (getPlayer().movementInput.jump) continue;
                ArrayList<Vec3d> packets = new ArrayList<>();
                for (int i = 1; i < getPlayer().posY - height; i++) {
                    if (getWorld().collidesWithAnyBlock(getPlayer().getEntityBoundingBox().offset(0, -i, 0))) {
                        if (!phase.getValue()) {
                            return;
                        }
                    } else {
                        packets.add(new Vec3d(getPlayer().posX, getPlayer().posY - i, getPlayer().posZ));
                    }
                }
                sendAll(packets);
                PositionUtils.move(resultPos.x, height, resultPos.z, 0, 0, true, false, DesyncMode.NONE);
                getPlayer().motionX = motionX;
                getPlayer().motionY = 0;
                getPlayer().motionZ = motionZ;
                return;
            }
        }
        if (!getWorld().collidesWithAnyBlock(getPlayer().getEntityBoundingBox().offset(direction.scale(0.01)))) return;
        if (normal.getValue()) {
            for (int y = 0; y <= height.getValue().getCurrent(); y++) {
                Vec3d pos = direction.scale(0.01).add(0, y, 0);
                AxisAlignedBB box = getPlayer().getEntityBoundingBox().offset(pos);
                AxisAlignedBB box2 = getPlayer().getEntityBoundingBox().offset(scaled).offset(pos);
                if (getWorld().collidesWithAnyBlock(box2)) continue;
                Vec3d resultPos = getPlayer().getPositionVector().add(scaled).add(pos);
                double height = getMaxHeight(box);
                ArrayList<Vec3d> packets = new ArrayList<>();
                for (int i = 1; i < height - getPlayer().posY; i++) {
                    if (getWorld().collidesWithAnyBlock(getPlayer().getEntityBoundingBox().offset(0, -i, 0))) {
                        if (!phase.getValue()) {
                            return;
                        }
                    } else {
                        packets.add(new Vec3d(getPlayer().posX, getPlayer().posY + i, getPlayer().posZ));
                    }
                }
                sendAll(packets);
                PositionUtils.move(resultPos.x, height, resultPos.z, 0, 0, true, false, DesyncMode.NONE);
                getPlayer().motionX = motionX;
                getPlayer().motionY = 0;
                getPlayer().motionZ = motionZ;
                return;
            }
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
