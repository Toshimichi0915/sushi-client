package net.toshimichi.sushi.modules.player;

import net.minecraft.network.play.client.CPacketPlayer;
import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.RootConfigurations;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.packet.PacketSendEvent;
import net.toshimichi.sushi.modules.*;

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
            getConnection().sendPacket(packet);
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
