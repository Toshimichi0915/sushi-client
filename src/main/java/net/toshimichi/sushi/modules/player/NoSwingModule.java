package net.toshimichi.sushi.modules.player;

import net.minecraft.network.play.client.CPacketAnimation;
import net.toshimichi.sushi.config.Configurations;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.packet.PacketSendEvent;
import net.toshimichi.sushi.modules.*;

public class NoSwingModule extends BaseModule {
    public NoSwingModule(String id, Modules modules, Categories categories, Configurations provider, ModuleFactory factory) {
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
    public void onPacketSend(PacketSendEvent e) {
        if (e.getPacket() instanceof CPacketAnimation) e.setCancelled(true);
    }

    @Override
    public String getDefaultName() {
        return "NoSwing";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.PLAYER;
    }
}
