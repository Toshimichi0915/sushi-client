package net.sushiclient.client.modules.movement;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.MovementInput;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.tick.ClientTickEvent;
import net.sushiclient.client.modules.*;

public class SprintModule extends BaseModule {

    private final Configuration<Boolean> multiDirection;

    public SprintModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        multiDirection = provider.get("multi", "Multi Direction", null, Boolean.class, false);
    }

    private boolean shouldSprint(MovementInput input) {
        if (multiDirection.getValue()) {
            return input.moveForward != 0 || input.moveStrafe != 0;
        } else {
            return input.moveForward > 0;
        }
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
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null) return;
        if (!shouldSprint(player.movementInput)) return;
        player.setSprinting(true);
    }

    @Override
    public String getDefaultName() {
        return "Sprint";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.MOVEMENT;
    }
}
