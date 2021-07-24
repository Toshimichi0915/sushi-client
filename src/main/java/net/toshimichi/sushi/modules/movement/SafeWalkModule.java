package net.toshimichi.sushi.modules.movement;

import net.minecraft.util.math.AxisAlignedBB;
import net.toshimichi.sushi.config.Config;
import net.toshimichi.sushi.config.ConfigInjector;
import net.toshimichi.sushi.config.RootConfigurations;
import net.toshimichi.sushi.config.data.DoubleRange;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.player.PlayerMoveEvent;
import net.toshimichi.sushi.modules.*;

public class SafeWalkModule extends BaseModule {

    @Config(id = "jump", name = "Jump")
    public Boolean jump = true;
    @Config(id = "height", name = "Height")
    public DoubleRange height = new DoubleRange(1.2, 20, 0, 0.2, 1);

    public SafeWalkModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        new ConfigInjector(provider).inject(this);
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
    }

    public boolean isSafe(AxisAlignedBB box) {
        double size = height.getCurrent();
        return getWorld().collidesWithAnyBlock(box.offset(0, -size / 2, 0).grow(0, size / 2, 0));
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onMove(PlayerMoveEvent e) {
        if (jump && (getPlayer().movementInput.jump || !getPlayer().onGround)) return;
        if (!isSafe(getPlayer().getEntityBoundingBox())) return;
        if (isSafe(getPlayer().getEntityBoundingBox().offset(e.getX(), 0, e.getZ()))) return;
        getPlayer().motionX = 0;
        getPlayer().motionZ = 0;
        e.setCancelled(true);
    }

    @Override
    public String getDefaultName() {
        return "SafeWalk";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.MOVEMENT;
    }
}
