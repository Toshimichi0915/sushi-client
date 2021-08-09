package net.toshimichi.sushi.handlers;

import net.minecraft.network.play.server.SPacketHeldItemChange;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.packet.PacketReceiveEvent;
import net.toshimichi.sushi.utils.player.InventoryUtils;

public class SilentSwitchHandler {

    @EventHandler(timing = EventTiming.PRE)
    public void onPacketReceive(PacketReceiveEvent e) {
        if (!(e.getPacket() instanceof SPacketHeldItemChange)) return;
        if (!InventoryUtils.isSwitching()) return;
        e.setCancelled(true);
    }
}
