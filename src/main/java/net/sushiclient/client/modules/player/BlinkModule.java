package net.sushiclient.client.modules.player;

import net.minecraft.network.play.client.CPacketPlayer;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.packet.PacketSendEvent;
import net.sushiclient.client.modules.*;

import java.util.ArrayList;

public class BlinkModule extends BaseModule {

    private final ArrayList<CPacketPlayer> packets = new ArrayList<>();
    private final Configuration<Boolean> all;

    public BlinkModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        all = provider.get("all", "All Packets", null, Boolean.class, true);
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
        for (CPacketPlayer packet : packets) {
            sendPacket(packet);
        }
        packets.clear();
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onPacketSend(PacketSendEvent e) {
        if (!all.getValue() && !(e.getPacket() instanceof CPacketPlayer)) return;
        packets.add((CPacketPlayer) e.getPacket());
        e.setCancelled(true);
    }

    @Override
    public String getDefaultName() {
        return "Blink";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.PLAYER;
    }
}
