package net.toshimichi.sushi.modules.client;

import net.toshimichi.sushi.config.RootConfigurations;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.client.ExceptionCatchEvent;
import net.toshimichi.sushi.modules.*;

public class AntiErrorKickModule extends BaseModule {
    public AntiErrorKickModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
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
    public void onExceptionCatch(ExceptionCatchEvent e) {
        e.setCancelled(true);
        e.getThrowable().printStackTrace();
    }

    @Override
    public String getDefaultName() {
        return "AntiErrorKick";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.CLIENT;
    }
}
