package net.sushiclient.client.modules.player;

import net.minecraft.network.play.server.SPacketEntityTeleport;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.packet.PacketReceiveEvent;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.utils.player.DesyncMode;
import net.sushiclient.client.utils.player.PositionUtils;

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
