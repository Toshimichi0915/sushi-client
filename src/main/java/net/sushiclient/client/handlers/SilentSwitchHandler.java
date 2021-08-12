package net.sushiclient.client.handlers;

import net.minecraft.network.play.server.SPacketHeldItemChange;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.packet.PacketReceiveEvent;
import net.sushiclient.client.utils.player.InventoryUtils;

public class SilentSwitchHandler {

    @EventHandler(timing = EventTiming.PRE)
    public void onPacketReceive(PacketReceiveEvent e) {
        if (!(e.getPacket() instanceof SPacketHeldItemChange)) return;
        if (!InventoryUtils.isSwitching()) return;
        e.setCancelled(true);
    }
}
