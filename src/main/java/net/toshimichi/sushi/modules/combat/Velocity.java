package net.toshimichi.sushi.modules.combat;

import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.toshimichi.sushi.config.Configurations;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.packet.PacketReceiveEvent;
import net.toshimichi.sushi.modules.*;

public class Velocity extends BaseModule {

    public Velocity(String id, Modules modules, Categories categories, Configurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
    }

    @Override
    public String getDefaultName() {
        return "Velocity";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.COMBAT;
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
    public void onPacketReceive(PacketReceiveEvent e) {
        if (e.getPacket() instanceof SPacketEntityVelocity) {
            e.setCancelled(true);
        }
    }
}
