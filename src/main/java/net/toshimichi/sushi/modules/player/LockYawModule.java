package net.toshimichi.sushi.modules.player;

import net.minecraft.util.math.Vec3d;
import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.Configurations;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.tick.ClientTickEvent;
import net.toshimichi.sushi.modules.*;
import net.toshimichi.sushi.utils.PositionUtils;
import net.toshimichi.sushi.utils.SyncMode;

public class LockYawModule extends BaseModule {

    private final Configuration<Integer> x;
    private final Configuration<Integer> z;

    public LockYawModule(String id, Modules modules, Categories categories, Configurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        x = provider.get("x", "X", null, Integer.class, 0);
        z = provider.get("z", "Z", null, Integer.class, 0);
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
    public void onClientTick(ClientTickEvent e) {
        PositionUtils.setSyncMode(SyncMode.BOTH);
        float pitch = PositionUtils.getPitch();
        PositionUtils.lookAt(new Vec3d(x.getValue(), 0, z.getValue()));
        getPlayer().rotationPitch = pitch;
    }

    @Override
    public String getDefaultName() {
        return "LockYaw";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.PLAYER;
    }
}
