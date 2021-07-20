package net.toshimichi.sushi.modules.world;

import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.RootConfigurations;
import net.toshimichi.sushi.config.data.IntRange;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.world.WorldTimeGetEvent;
import net.toshimichi.sushi.modules.*;
import net.toshimichi.sushi.utils.TickUtils;

public class WorldTimeModule extends BaseModule {

    private final Configuration<Boolean> fastForward;
    private final Configuration<IntRange> time;

    public WorldTimeModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        fastForward = provider.get("fast_forward", "Fast Forward", null, Boolean.class, false);
        time = provider.get("time", "Time", null, IntRange.class, new IntRange(12, 24, 0, 1), () -> !fastForward.getValue(), false, 0);
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
    }

    public long getWorldTime() {
        if (fastForward.getValue()) {
            return (long) ((TickUtils.current() + getClient().getRenderPartialTicks()) * 100L);
        } else {
            return time.getValue().getCurrent() * 1000L;
        }
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onWorldTimeGet(WorldTimeGetEvent e) {
        e.setWorldTime(getWorldTime());
    }

    @Override
    public String getDefaultName() {
        return "WorldTime";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.WORLD;
    }
}
