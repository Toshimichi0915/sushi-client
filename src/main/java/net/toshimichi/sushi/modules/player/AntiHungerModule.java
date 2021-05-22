package net.toshimichi.sushi.modules.player;

import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.Configurations;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.packet.PacketSendEvent;
import net.toshimichi.sushi.events.player.PlayerPacketEvent;
import net.toshimichi.sushi.modules.*;

public class AntiHungerModule extends BaseModule {

    private final Configuration<Boolean> effective;
    private boolean sprinting;
    private boolean onGround;

    public AntiHungerModule(String id, Modules modules, Categories categories, Configurations provider, ModuleFactory factory) {
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
