package net.sushiclient.client.modules.movement;

import net.minecraft.util.MovementInput;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.config.data.DoubleRange;
import net.sushiclient.client.config.data.Named;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.player.PlayerTravelEvent;
import net.sushiclient.client.events.tick.ClientTickEvent;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.utils.EntityUtils;
import net.sushiclient.client.utils.TimerUtils;
import net.sushiclient.client.utils.player.MovementUtils;
import net.sushiclient.client.utils.world.BlockUtils;

public class SpeedModule extends BaseModule {

    private final Configuration<SpeedMode> mode;
    private final Configuration<DoubleRange> multiplier;
    private final Configuration<Boolean> forceSprint;
    private final Configuration<Boolean> enableOnJump;
    private final Configuration<Boolean> resetMotion;
    private final Configuration<Boolean> fastJump;
    private final Configuration<Boolean> jumpSkip;
    private final Configuration<DoubleRange> factor;
    private boolean lastActive;
    private int counter;

    public SpeedModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        mode = provider.get("mode", "Mode", null, SpeedMode.class, SpeedMode.STRAFE);
        multiplier = provider.get("multiplier", "Multiplier", null, DoubleRange.class,
                new DoubleRange(1.5, 5, 0.1, 0.05, 2), () -> mode.getValue() == SpeedMode.VANILLA, false, 0);
        forceSprint = provider.get("force_sprint", "Force Sprint", null, Boolean.class, true);
        enableOnJump = provider.get("enable_on_jump", "Enable On Jump", null, Boolean.class, false);
        resetMotion = provider.get("reset_motion", "Reset Motion", null, Boolean.class, true);
        fastJump = provider.get("fast_jump", "Fast Jump", null, Boolean.class, true);
        jumpSkip = provider.get("jump_skip", "Jump Skip", null, Boolean.class, true);
        factor = provider.get("factor", "Factor", null, DoubleRange.class, new DoubleRange(1.1, 2, 1, 0.01, 2));
        factor.addHandler(d -> {
            if (!isEnabled()) return;
            TimerUtils.pop(counter);
            counter = TimerUtils.push((float) factor.getValue().getCurrent());
        });
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
        counter = TimerUtils.push((float) factor.getValue().getCurrent());
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
        resetMotion();
        TimerUtils.pop(counter);
    }

    private void resetMotion() {
        if (!resetMotion.getValue() || !lastActive) return;
        lastActive = false;
        getPlayer().motionX = 0;
        getPlayer().motionZ = 0;
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onClientTick(ClientTickEvent e) {
        if (forceSprint.getValue() &&
                MovementUtils.getMoveInputs(getPlayer()).squareDistanceTo(Vec3d.ZERO) > 0.1) {
            getPlayer().setSprinting(true);
        }
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onTravel(PlayerTravelEvent e) {
        MovementInput input = getPlayer().movementInput;
        if (enableOnJump.getValue() && !input.jump) {
            resetMotion();
            return;
        }

        lastActive = true;

        Vec3d moveInputs = MovementUtils.getMoveInputs(getPlayer());
        moveInputs = moveInputs.subtract(0, moveInputs.y, 0).normalize();
        Vec2f motion = MovementUtils.toWorld(new Vec2f((float) moveInputs.x, (float) moveInputs.z), getPlayer().rotationYaw);

        AxisAlignedBB box = getPlayer().getEntityBoundingBox().offset(motion.x / 10, 0, motion.y / 10);

        if (fastJump.getValue() && input.jump && EntityUtils.isOnGround(getPlayer())) {
            getPlayer().motionY = 0.42;
        }

        if (jumpSkip.getValue() &&
                getWorld().collidesWithAnyBlock(box) &&
                !getWorld().collidesWithAnyBlock(box.offset(0, 0.25, 0)) &&
                getPlayer().motionY > 0) {
            getPlayer().setPosition(getPlayer().posX + motion.x / 10,
                    BlockUtils.getMaxHeight(box.offset(0, 0.25, 0)),
                    getPlayer().posZ + motion.y / 10);
            getPlayer().motionY = 0;
        }

        if (mode.getValue() == SpeedMode.VANILLA) {
            float value = (float) multiplier.getValue().getCurrent();
            getPlayer().motionX = motion.x * value;
            getPlayer().motionZ = motion.y * value;
        } else if (mode.getValue() == SpeedMode.STRAFE) {
            Vec3d vec = new Vec3d(getPlayer().motionX, 0, getPlayer().motionZ);
            double mul = Math.max(vec.distanceTo(Vec3d.ZERO), getPlayer().isSneaking() ? 0 : 0.1);
            if (!input.forwardKeyDown && EntityUtils.isOnGround(getPlayer())) {
                mul *= 1.18;
            } else if (!input.forwardKeyDown) {
                mul *= 1.025;
            }
            getPlayer().motionX = motion.x * mul;
            getPlayer().motionZ = motion.y * mul;
        }
    }

    @Override
    public String getDefaultName() {
        return "Speed";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.MOVEMENT;
    }

    public enum SpeedMode implements Named {
        VANILLA("Vanilla"), STRAFE("Strafe");
        private final String name;

        SpeedMode(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
