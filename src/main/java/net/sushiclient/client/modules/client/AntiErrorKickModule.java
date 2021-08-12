package net.sushiclient.client.modules.client;

import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.client.ExceptionCatchEvent;
import net.sushiclient.client.modules.*;

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
