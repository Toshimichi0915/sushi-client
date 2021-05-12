package net.toshimichi.sushi.modules.movement;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.MoverType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.Configurations;
import net.toshimichi.sushi.config.data.DoubleRange;
import net.toshimichi.sushi.config.data.IntRange;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.player.PlayerMotionEvent;
import net.toshimichi.sushi.events.player.PlayerPushEvent;
import net.toshimichi.sushi.modules.*;
import net.toshimichi.sushi.utils.MovementUtils;
import net.toshimichi.sushi.utils.PositionUtils;
import net.toshimichi.sushi.utils.SyncMode;

import java.util.List;

public class PhaseWalkModule extends BaseModule {

    private final Configuration<DoubleRange> horizontal;
    private final Configuration<DoubleRange> delta;
    private final Configuration<IntRange> range;
    private boolean clipping;

    public PhaseWalkModule(String id, Modules modules, Categories categories, Configurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        horizontal = provider.get("horizontal_speed", "Horizontal Speed", null, DoubleRange.class, new DoubleRange(1, 20, 0, 0.1, 1));
        delta = provider.get("delta", "Delta", null, DoubleRange.class, new DoubleRange(0.5, 1, 0.1, 0.1, 1));
        range = provider.get("range", "Range", null, IntRange.class, new IntRange(3, 8, 0, 1));
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
        if (clipping) {
            PositionUtils.setSyncMode(SyncMode.BOTH);
            PositionUtils.move(PositionUtils.getX(), PositionUtils.getY(), PositionUtils.getZ(), 0, 0, true, false);
        }
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onPrePlayerMotion(PlayerMotionEvent e) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        player.noClip = true;
        player.onGround = true;
        if (e.getType() != MoverType.SELF) {
            e.setCancelled(true);
            return;
        }

        double horizontalSpeed = horizontal.getValue().getCurrent() / 10;
        Vec3d inputs = MovementUtils.getMoveInputs(player);
        float moveForward = (float) (inputs.x * horizontalSpeed);
        float moveStrafe = (float) (inputs.z * horizontalSpeed);

        Vec2f vec = MovementUtils.toWorld(new Vec2f(moveForward, moveStrafe), player.rotationYaw);
        player.motionX = vec.x;
        player.motionY = 0;
        player.motionZ = vec.y;
        e.setX(vec.x);
        e.setY(0);
        e.setZ(vec.y);
    }

    @EventHandler(timing = EventTiming.POST)
    public void onPostPlayerMotion(PlayerMotionEvent e) {
        clipping = false;
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (e.getType() != MoverType.SELF) {
            e.setCancelled(true);
            return;
        }
        int step = range.getValue().getCurrent();
        boolean isAboveAir = false;
        for (int y = step; y >= -step; y--) {
            AxisAlignedBB boundingBox = player.getEntityBoundingBox().offset(0, y, 0).shrink(0.5);
            List<AxisAlignedBB> collisions = player.world.getCollisionBoxes(null, boundingBox);
            if (collisions.isEmpty()) {
                isAboveAir = true;
            } else if (isAboveAir) {
                clipping = true;
                double maxY = 0;
                for (AxisAlignedBB collision : collisions) {
                    if (collision.maxY > maxY) {
                        maxY = collision.maxY;
                    }
                }
                PositionUtils.setSyncMode(SyncMode.BOTH);
                PositionUtils.move(player.posX, maxY, player.posZ, 0, 0, true, false);
                PositionUtils.setSyncMode(SyncMode.LOOK);
                PositionUtils.move(player.posX, maxY - delta.getValue().getCurrent(), player.posZ, 0, 0, true, false);
                return;
            }
        }

        // safe walk
        PositionUtils.setSyncMode(SyncMode.BOTH);
        PositionUtils.move(player.prevPosX, player.prevPosY, player.prevPosZ, 0, 0, true, false);
        PositionUtils.setSyncMode(SyncMode.LOOK);
        PositionUtils.move(player.prevPosX, PositionUtils.getY(), player.prevPosZ, 0, 0, true, false);
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onPush(PlayerPushEvent e) {
        e.setCancelled(true);
    }

    @Override
    public String getDefaultName() {
        return "PhaseWalk";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.MOVEMENT;
    }
}
