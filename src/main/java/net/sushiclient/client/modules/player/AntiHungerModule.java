package net.sushiclient.client.modules.player;

import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.packet.PacketSendEvent;
import net.sushiclient.client.events.player.PlayerPacketEvent;
import net.sushiclient.client.modules.*;

public class AntiHungerModule extends BaseModule {

    private final Configuration<Boolean> effective;
    private boolean sprinting;
    private boolean onGround;

    public AntiHungerModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        effective = provider.get("effective", "Effective", null, Boolean.class, false);
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
    }

    @EventHandler(timing = EventTiming.PRE, priority = -1000)
    public void onPacketSend(PlayerPacketEvent e) {
        sprinting = getPlayer().isSprinting();
        getPlayer().setSprinting(false);
        if (effective.getValue()) {
            onGround = getPlayer().onGround;
            getPlayer().onGround = true;
        }
    }

    @EventHandler(timing = EventTiming.POST, priority = -1000)
    public void onPacketSend(PacketSendEvent e) {
        getPlayer().setSprinting(sprinting);
        if (effective.getValue()) {
            getPlayer().onGround = onGround;
        }
    }

    @Override
    public String getDefaultName() {
        return "AntiHunger";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.PLAYER;
    }
}
