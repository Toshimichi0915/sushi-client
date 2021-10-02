package net.sushiclient.client.modules.movement;

import net.minecraft.util.math.AxisAlignedBB;
import net.sushiclient.client.config.Config;
import net.sushiclient.client.config.ConfigInjector;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.config.data.DoubleRange;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.player.PlayerMoveEvent;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.utils.EntityUtils;

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
        if (jump && (getPlayer().movementInput.jump || !EntityUtils.isOnGround(getPlayer()))) return;
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
