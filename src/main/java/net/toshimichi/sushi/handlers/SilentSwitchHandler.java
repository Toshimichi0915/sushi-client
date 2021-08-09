package net.toshimichi.sushi.handlers;

import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.packet.PacketSendEvent;
import net.toshimichi.sushi.utils.player.InventoryUtils;

public class SilentSwitchHandler {

    @EventHandler(timing = EventTiming.PRE)
    public void onPacketSend(PacketSendEvent e) {
        if (!(e.getPacket() instanceof CPacketHeldItemChange)) return;
        int slot = ((CPacketHeldItemChange) e.getPacket()).getSlotId();
        if (!InventoryUtils.isSwitching()) return;
        if (InventoryUtils.getHotbarSlot() == slot) return;
        e.setCancelled(true);
    }
}
