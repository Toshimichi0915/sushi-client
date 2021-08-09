package net.toshimichi.sushi.modules.movement;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.RootConfigurations;
import net.toshimichi.sushi.config.data.DoubleRange;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.player.PlayerPacketEvent;
import net.toshimichi.sushi.events.player.PlayerTravelEvent;
import net.toshimichi.sushi.modules.*;
import net.toshimichi.sushi.utils.EntityUtils;
import net.toshimichi.sushi.utils.TpsUtils;
import net.toshimichi.sushi.utils.player.DesyncMode;
import net.toshimichi.sushi.utils.player.MovementUtils;
import net.toshimichi.sushi.utils.player.PositionUtils;

public class PhaseFlyModule extends BaseModule {

    private final Configuration<DoubleRange> horizontal;
    private final Configuration<DoubleRange> vertical;
    private final Configuration<DoubleRange> elytraHorizontal;
    private final Configuration<DoubleRange> elytraVertical;
    private final Configuration<Boolean> auto;
    private final Configuration<Boolean> tpsSync;
    private final Configuration<Boolean> capAt20;
    private int stage;

    public PhaseFlyModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        horizontal = provider.get("horizontal_speed", "Horizontal Speed", null, DoubleRange.class, new DoubleRange(1, 1, 0, 0.05, 2));
        vertical = provider.get("vertical_speed", "Vertical Speed", null, DoubleRange.class, new DoubleRange(1, 1, 0, 0.05, 2));
        elytraHorizontal = provider.get("elytra_horizontal_speed", "Horizontal Speed(Elytra)", null, DoubleRange.class, new DoubleRange(1, 1, 0, 0.05, 2));
        elytraVertical = provider.get("elytra_vertical_speed", "Vertical Speed(Elytra)", null, DoubleRange.class, new DoubleRange(1, 1, 0, 0.05, 2));
        auto = provider.get("auto", "Auto Phase", null, Boolean.class, true);
        tpsSync = provider.get("tps_sync", "TPS Sync", null, Boolean.class, false);
        capAt20 = provider.get("cap_at_20", "Cap At 20", null, Boolean.class, false, tpsSync::getValue, false, 0);
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
    public void onPlayerTravel(PlayerTravelEvent e) {
        EntityPlayerSP player = getPlayer();
        player.motionX = 0;
        player.motionY = 0;
        player.motionZ = 0;
        if (stage != 0) return;
        player.noClip = EntityUtils.isInsideBlock(getPlayer());
        player.fallDistance = 0;
        player.onGround = false;

        double horizontalSpeed;
        double verticalSpeed;
        if (player.isElytraFlying()) {
            horizontalSpeed = elytraHorizontal.getValue().getCurrent();
            verticalSpeed = elytraVertical.getValue().getCurrent();
        } else {
            horizontalSpeed = horizontal.getValue().getCurrent();
            verticalSpeed = vertical.getValue().getCurrent();
        }
        horizontalSpeed *= 5;
        verticalSpeed *= 5;
        Vec3d inputs = MovementUtils.getMoveInputs(player).normalize();
        float moveForward = (float) (inputs.x * horizontalSpeed);
        float moveUpward = (float) (inputs.y * verticalSpeed);
        float moveStrafe = (float) (inputs.z * horizontalSpeed);

        if (tpsSync.getValue()) {
            double tps = TpsUtils.getTps();
            if (capAt20.getValue()) tps = Math.max(20, tps);
            moveForward *= tps / 20;
            moveStrafe *= tps / 20;
            moveUpward *= tps / 20;
        }

        Vec2f vec = MovementUtils.toWorld(new Vec2f(moveForward, moveStrafe), player.rotationYaw);
        player.motionX = vec.x;
        player.motionY = moveUpward;
        player.motionZ = vec.y;
        // Anti Glide
        if (player.isElytraFlying()) {
            float f = player.rotationPitch * 0.017453292F;
            double d1 = player.getLookVec().length();
            float f4 = MathHelper.cos(f);
            f4 = (float) ((double) f4 * (double) f4 * Math.min(1.0D, d1 / 0.4D));
            player.motionY -= -0.08D + (double) f4 * 0.06D;
        }
    }

    private boolean isHittingRoof() {
        return getWorld().collidesWithAnyBlock(getPlayer().getEntityBoundingBox().offset(0, 0.01, 0));
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onPlayerPacket(PlayerPacketEvent e) {
        if (!auto.getValue()) return;
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (stage == 0 &&
                (getPlayer().movementInput.sneak || EntityUtils.isInsideBlock(getPlayer()) ||
                        !EntityUtils.isInsideBlock(getPlayer()) && !isHittingRoof())) return;
        if (stage == 0 || stage == 1) {
            player.movementInput.sneak = true;
            stage++;
        } else if (stage == 2) {
            player.movementInput.sneak = true;
            PositionUtils.move(player.posX, player.posY + 0.1, player.posZ, 0, 0, true, false, DesyncMode.NONE);
            stage++;
        } else if (stage == 3) {
            stage = 0;
        }
    }

    @Override
    public String getDefaultName() {
        return "PhaseFly";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.MOVEMENT;
    }
}
