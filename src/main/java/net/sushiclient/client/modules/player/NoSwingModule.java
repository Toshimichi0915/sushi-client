package net.sushiclient.client.modules.player;

import net.minecraft.network.play.client.CPacketAnimation;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.packet.PacketSendEvent;
import net.sushiclient.client.modules.*;

public class NoSwingModule extends BaseModule {
    public NoSwingModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
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
