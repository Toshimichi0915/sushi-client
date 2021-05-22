package net.toshimichi.sushi.modules.movement;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.Configurations;
import net.toshimichi.sushi.config.data.DoubleRange;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.player.PlayerPacketEvent;
import net.toshimichi.sushi.events.player.PlayerPushEvent;
import net.toshimichi.sushi.events.player.PlayerTravelEvent;
import net.toshimichi.sushi.events.player.PlayerUpdateEvent;
import net.toshimichi.sushi.modules.*;
import net.toshimichi.sushi.utils.DesyncMode;
import net.toshimichi.sushi.utils.MovementUtils;
import net.toshimichi.sushi.utils.PositionUtils;
import net.toshimichi.sushi.utils.TpsUtils;

public class PhaseFlyModule extends BaseModule {

    private final Configuration<DoubleRange> horizontal;
    private final Configuration<DoubleRange> vertical;
    private final Configuration<Boolean> auto;
    private final Configuration<Boolean> tpsSync;
    private int stage;

    // for compatibility issue
    private boolean noClip;
    private boolean collidedVertically;

    public PhaseFlyModule(String id, Modules modules, Categories categories, Configurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        horizontal = provider.get("horizontal_speed", "Horizontal Speed", null, DoubleRange.class, new DoubleRange(1, 20, 0, 0.1, 1));
        vertical = provider.get("vertical_speed", "Vertical Speed", null, DoubleRange.class, new DoubleRange(1, 20, 0, 0.1, 1));
        auto = provider.get("auto", "Auto", null, Boolean.class, false);
        tpsSync = provider.get("tps_sync", "TPS Sync", null, Boolean.class, false);
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
        if (stage != 0) {
            player.motionX = 0;
            player.motionY = 0;
            player.motionZ = 0;
            return;
        }
        player.noClip = !player.world.getCollisionBoxes(null, player.getEntityBoundingBox()).isEmpty();
        player.fallDistance = 0;
        player.onGround = false;

        double horizontalSpeed = horizontal.getValue().getCurrent() / 10;
        double verticalSpeed = vertical.getValue().getCurrent() / 10;
        Vec3d inputs = MovementUtils.getMoveInputs(player);
        float moveForward = (float) (inputs.x * horizontalSpeed);
        float moveUpward = (float) (inputs.y * verticalSpeed);
        float moveStrafe = (float) (inputs.z * horizontalSpeed);

        if (tpsSync.getValue()) {
            moveForward *= TpsUtils.getTps() / 20;
            moveStrafe *= TpsUtils.getTps() / 20;
            moveUpward *= TpsUtils.getTps() / 20;
        }

        Vec2f vec = MovementUtils.toWorld(new Vec2f(moveForward, moveStrafe), player.rotationYaw);
        player.motionX = vec.x;
        player.motionY = moveUpward;
        player.motionZ = vec.y;
    }

    @EventHandler(timing = EventTiming.POST)
    public void onPlayerUpdate(PlayerUpdateEvent e) {
        noClip = getPlayer().noClip;
        collidedVertically = getPlayer().collidedVertically;
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onPlayerPacket(PlayerPacketEvent e) {
        if (!auto.getValue()) return;
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (stage == 0 && (getPlayer().movementInput.sneak || noClip || !collidedVertically)) return;
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

    @EventHandler(timing = EventTiming.PRE)
    public void onPush(PlayerPushEvent e) {
        e.setCancelled(true);
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
