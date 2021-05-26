package net.toshimichi.sushi.modules.player;

import net.minecraft.network.play.server.SPacketEntityTeleport;
import net.toshimichi.sushi.config.RootConfigurations;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.packet.PacketReceiveEvent;
import net.toshimichi.sushi.modules.*;
import net.toshimichi.sushi.utils.DesyncMode;
import net.toshimichi.sushi.utils.PositionUtils;

public class AntiStuckModule extends BaseModule {

    public AntiStuckModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
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

    @EventHandler(timing = EventTiming.POST)
    public void onPlayerTravel(PacketReceiveEvent e) {
        if (!(e.getPacket() instanceof SPacketEntityTeleport)) return;
        SPacketEntityTeleport packet = (SPacketEntityTeleport) e.getPacket();
        if (getPlayer().getEntityId() != packet.getEntityId()) return;
        getClient().addScheduledTask(() -> {
            PositionUtils.move(getPlayer().posX, getPlayer().posY + 0.1, getPlayer().posZ, 0, 0, true, false, DesyncMode.NONE);
        });
    }

    @Override
    public String getDefaultName() {
        return "AntiStuck";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.PLAYER;
    }
}
