package net.sushiclient.client.modules.movement;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
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
import net.sushiclient.client.utils.UpdateTimer;
import net.sushiclient.client.utils.player.PositionPacketUtils;
import net.sushiclient.client.utils.render.hole.HoleUtils;
import net.sushiclient.client.utils.world.BlockUtils;

public class StepModule extends BaseModule implements ModuleSuffix {

    private final Configuration<StepMode> stepMode;
    private final Configuration<Boolean> phase;
    private final Configuration<Boolean> normal;
    private final Configuration<IntRange> height;
    private final Configuration<DoubleRange> delta;

    private final Configuration<Boolean> reverse;
    private final Configuration<IntRange> reverseHeight;
    private final Configuration<DoubleRange> reverseMinHeight;

    private final Configuration<Boolean> pauseInHole;
    private final Configuration<Boolean> pauseOnSneak;
    private final Configuration<IntRange> packetCapacity;
    private final Configuration<IntRange> packetLimit;
    private double motionX;
    private double motionZ;
    private double groundY;
    private int threshold;
    private UpdateTimer packetTimer;

    public StepModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        stepMode = provider.get("mode", "Mode", null, StepMode.class, StepMode.NCP);
        phase = provider.get("phase", "Phase", null, Boolean.class, true);
        normal = provider.get("normal", "Normal", null, Boolean.class, true);
        height = provider.get("height", "Height", null, IntRange.class, new IntRange(2, 8, 1, 1), normal::getValue, false, 0);
        delta = provider.get("delta", "Delta", null, DoubleRange.class, new DoubleRange(0.1, 1, 0, 0.1, 1), normal::getValue, false, 0);

        reverse = provider.get("reverse", "Reverse", null, Boolean.class, true);
        reverseHeight = provider.get("reverse_height", "Reverse Height", null, IntRange.class, new IntRange(2, 8, 1, 1), reverse::getValue, false, 0);
        reverseMinHeight = provider.get("reverse_min_height", "Reverse Min Height", null, DoubleRange.class, new DoubleRange(0.3, 1, 0, 0.1, 1), reverse::getValue, false, 0);

        pauseInHole = provider.get("pause_in_hole", "Pause In Hole", null, Boolean.class, true);
        pauseOnSneak = provider.get("pause_on_sneak", "Pause On Sneak", null, Boolean.class, true);
        packetCapacity = provider.get("packet_capacity", "Packet Capacity", null, IntRange.class, new IntRange(40, 100, 0, 1));
        packetLimit = provider.get("packet_limit", "Packet Limit", null, IntRange.class, new IntRange(22, 100, 0, 1));
        packetTimer = new UpdateTimer(true, 1000);
        PositionPacketUtils.addListener(this, it -> {
            threshold = MathHelper.clamp(threshold + 1, 0,
                    packetCapacity.getValue().getCurrent() + packetLimit.getValue().getCurrent());
            if (packetTimer.update()) {
                threshold -= packetLimit.getValue().getCurrent();
            }
        });
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
    }

    @EventHandler(timing = EventTiming.PRE, priority = 50000)
    public void onPrePlayerMove(PlayerMoveEvent e) {
        motionX = getPlayer().motionX;
        motionZ = getPlayer().motionZ;
        if (EntityUtils.isOnGround(getPlayer())) {
            groundY = getPlayer().posY;
        }
    }

    @EventHandler(timing = EventTiming.POST)
    public void onPostPlayerMove(PlayerMoveEvent e) {
        if (threshold > packetCapacity.getValue().getCurrent()) return;
        if (EntityUtils.isInsideBlock(getPlayer())) return;
        if (getPlayer().posY > groundY) return;
        if (getPlayer().fallDistance > 0.1) return;
        if (getPlayer().movementInput.jump) return;
        if (getPlayer().isInWater()) return;
        if (getPlayer().isInLava()) return;
        if (getPlayer().isOnLadder()) return;
        BlockPos floorPos = BlockUtils.toBlockPos(getPlayer().getPositionVector());
        if (pauseInHole.getValue() && HoleUtils.getHoleInfo(getWorld(), floorPos, false) != null ||
                pauseOnSneak.getValue() && getPlayer().isSneaking()) {
            return;
        }
        Vec3d direction = new Vec3d(motionX, 0, motionZ).normalize().scale(delta.getValue().getCurrent());

        if (reverse.getValue()) {
            for (int y = -reverseHeight.getValue().getCurrent(); y < 0; y++) {
                AxisAlignedBB box = getPlayer().getEntityBoundingBox()
                        .offset(direction)
                        .offset(0, y + 0.99, 0);
                if (getWorld().collidesWithAnyBlock(box)) continue;
                double height = BlockUtils.getMaxHeight(box);
                double dY = height - getPlayer().posY;
                if (Double.isNaN(height)) continue;
                if (dY < y) continue;
                if (-dY < reverseMinHeight.getValue().getCurrent()) continue;
                StepMode mode = stepMode.getValue();
                if (mode.reverse(direction.x, dY, direction.z, height, phase.getValue())) return;
            }
        }

        if (normal.getValue()) {
            for (int y = height.getValue().getCurrent(); y > 0; y--) {
                AxisAlignedBB box = getPlayer().getEntityBoundingBox()
                        .offset(direction)
                        .offset(0, y, 0);
                if (getWorld().collidesWithAnyBlock(box)) continue;
                double height = BlockUtils.getMaxHeight(box);
                double dY = height - getPlayer().posY;
                if (Double.isNaN(height)) continue;
                if (dY > y) continue;
                StepMode mode = stepMode.getValue();
                if (mode.step(direction.x, dY, direction.z, height, phase.getValue())) {
                    getPlayer().motionX = motionX;
                    getPlayer().motionZ = motionZ;
                    return;
                }
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

    @Override
    public String getSuffix() {
        return stepMode.getValue().getName();
    }
}
