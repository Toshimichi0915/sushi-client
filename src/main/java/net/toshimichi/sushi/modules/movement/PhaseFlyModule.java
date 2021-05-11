package net.toshimichi.sushi.modules.movement;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.MoverType;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.Configurations;
import net.toshimichi.sushi.config.data.DoubleRange;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.player.PlayerMotionEvent;
import net.toshimichi.sushi.events.player.PlayerPushEvent;
import net.toshimichi.sushi.events.player.PlayerUpdateEvent;
import net.toshimichi.sushi.modules.*;
import net.toshimichi.sushi.utils.MovementUtils;
import net.toshimichi.sushi.utils.PositionUtils;
import net.toshimichi.sushi.utils.SyncMode;

public class PhaseFlyModule extends BaseModule {

    private final Configuration<DoubleRange> horizontal;
    private final Configuration<DoubleRange> vertical;
    private final Configuration<Boolean> auto;
    private int stage;

    public PhaseFlyModule(String id, Modules modules, Categories categories, Configurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        horizontal = provider.get("horizontal_speed", "Horizontal Speed", null, DoubleRange.class, new DoubleRange(1, 20, 0, 0.1, 1));
        vertical = provider.get("vertical_speed", "Vertical Speed", null, DoubleRange.class, new DoubleRange(1, 20, 0, 0.1, 1));
        auto = provider.get("auto", "Auto", null, Boolean.class, false);
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
    public void onMotion(PlayerMotionEvent e) {
        if (e.getType() != MoverType.SELF || stage != 0) {
            e.setCancelled(true);
        }
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        player.noClip = !player.world.getCollisionBoxes(null, player.getEntityBoundingBox()).isEmpty();
        player.fallDistance = 0;
        player.onGround = false;

        double horizontalSpeed = horizontal.getValue().getCurrent() / 10;
        double verticalSpeed = vertical.getValue().getCurrent() / 10;
        Vec3d inputs = MovementUtils.getMoveInputs(player);
        float moveForward = (float) (inputs.x * horizontalSpeed);
        float moveUpward = (float) (inputs.y * verticalSpeed);
        float moveStrafe = (float) (inputs.z * horizontalSpeed);

        Vec2f vec = MovementUtils.toWorld(new Vec2f(moveForward, moveStrafe), player.rotationYaw);
        player.motionX = vec.x;
        player.motionY = moveUpward;
        player.motionZ = vec.y;
        e.setX(vec.x);
        e.setY(moveUpward);
        e.setZ(vec.y);
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onPlayerUpdate(PlayerUpdateEvent e) {
        if (!auto.getValue()) return;
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (stage == 0 && (player.movementInput.sneak || player.noClip || !player.collidedVertically)) return;
        if (stage == 0) {
            player.movementInput.sneak = true;
            stage++;
        } else if (stage == 1) {
            player.movementInput.sneak = true;
            PositionUtils.setSyncMode(SyncMode.BOTH);
            PositionUtils.move(player.posX, player.posY + 0.1, player.posZ, 0, 0, true, false);
            stage++;
        } else if (stage == 2) {
            stage = 0;
        }
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onPush(PlayerPushEvent e) {
        e.setCancelled(true);
    }

    @Override
    public String getDefaultName() {
        return "Phase";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.MOVEMENT;
    }
}
