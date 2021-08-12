package net.sushiclient.client.modules.movement;

import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.config.data.DoubleRange;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.player.PlayerUpdateEvent;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.utils.player.MovementUtils;

public class SpeedModule extends BaseModule {

    private final Configuration<DoubleRange> multiplier;
    private final Configuration<Boolean> enableOnJump;
    private final Configuration<Boolean> resetMotion;
    private boolean lastActive;

    public SpeedModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        multiplier = provider.get("multiplier", "Multiplier", null, DoubleRange.class, new DoubleRange(1.5, 5, 0.1, 0.05, 2));
        enableOnJump = provider.get("enable_on_jump", "Enable On Jump", null, Boolean.class, false);
        resetMotion = provider.get("reset_motion", "Reset Motion", null, Boolean.class, true);
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
        resetMotion();
    }

    private void resetMotion() {
        if (!resetMotion.getValue() || !lastActive) return;
        lastActive = false;
        getPlayer().motionX = 0;
        getPlayer().motionZ = 0;
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onUpdate(PlayerUpdateEvent e) {
        if (enableOnJump.getValue() && !getPlayer().movementInput.jump) {
            resetMotion();
            return;
        }
        lastActive = true;
        float value = (float) multiplier.getValue().getCurrent();
        Vec3d moveInputs = MovementUtils.getMoveInputs(getPlayer()).normalize();
        Vec2f motion = MovementUtils.toWorld(new Vec2f((float) moveInputs.x, (float) moveInputs.z), getPlayer().rotationYaw);
        getPlayer().motionX = motion.x * value;
        getPlayer().motionZ = motion.y * value;
    }

    @Override
    public String getDefaultName() {
        return "Speed";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.MOVEMENT;
    }
}
