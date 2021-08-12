package net.sushiclient.client.modules.movement;

import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.input.InputUpdateEvent;
import net.sushiclient.client.modules.*;

public class AutoWalkModule extends BaseModule {

    private final Configuration<Boolean> input;

    public AutoWalkModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        input = provider.get("input", "Input", null, Boolean.class, true);
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
    }

    @EventHandler(timing = EventTiming.POST)
    public void onInputUpdate(InputUpdateEvent e) {
        if (input.getValue()) getPlayer().movementInput.forwardKeyDown = true;
        getPlayer().movementInput.moveForward = 1.0F;
    }

    @Override
    public String getDefaultName() {
        return "AutoWalk";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.MOVEMENT;
    }
}
