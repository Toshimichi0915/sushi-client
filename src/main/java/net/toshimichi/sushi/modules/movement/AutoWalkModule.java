package net.toshimichi.sushi.modules.movement;

import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.RootConfigurations;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.input.InputUpdateEvent;
import net.toshimichi.sushi.modules.*;

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
