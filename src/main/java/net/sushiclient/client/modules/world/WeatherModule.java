package net.sushiclient.client.modules.world;

import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.world.RainStrengthGetEvent;
import net.sushiclient.client.events.world.ThunderStrengthGetEvent;
import net.sushiclient.client.modules.*;

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
