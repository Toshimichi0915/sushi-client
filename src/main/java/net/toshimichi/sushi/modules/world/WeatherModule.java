package net.toshimichi.sushi.modules.world;

import net.toshimichi.sushi.config.RootConfigurations;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.world.RainStrengthGetEvent;
import net.toshimichi.sushi.events.world.ThunderStrengthGetEvent;
import net.toshimichi.sushi.modules.*;

public class WeatherModule extends BaseModule {

    public WeatherModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
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
    public void onRainStrengthGetEvent(RainStrengthGetEvent e) {
        e.setValue(0);
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onThunderStrengthGetEvent(ThunderStrengthGetEvent e) {
        e.setValue(0);
    }

    @Override
    public String getDefaultName() {
        return "Weather";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.WORLD;
    }
}
