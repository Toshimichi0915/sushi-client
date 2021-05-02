package net.toshimichi.sushi.modules.movement;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.MoverType;
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

public class PhaseModule extends BaseModule {

    private final Configuration<DoubleRange> horizontal;
    private final Configuration<DoubleRange> vertical;
    private boolean noGravity;

    public PhaseModule(String id, Modules modules, Categories categories, Configurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        horizontal = provider.get("horizontal_speed", "Horizontal Speed", null, DoubleRange.class, new DoubleRange(1, 10, 0, 0.1, 1));
        vertical = provider.get("vertical_speed", "Vertical Speed", null, DoubleRange.class, new DoubleRange(1, 10, 0, 0.1, 1));
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
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        player.noClip = true;
        if (e.getType() == MoverType.SELF) {
            e.setX(e.getX() * horizontal.getValue().getCurrent());
            e.setZ(e.getZ() * horizontal.getValue().getCurrent());
        } else {
            e.setCancelled(true);
        }
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onUpdate(PlayerUpdateEvent e) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        player.fallDistance = 0;
        player.onGround = false;
        player.motionY = 0;
        if (player.movementInput.jump) {
            player.move(MoverType.SELF, 0, vertical.getValue().getCurrent() / 10, 0);
        }
        if (player.movementInput.sneak) {
            player.move(MoverType.SELF, 0, -vertical.getValue().getCurrent() / 10, 0);
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
