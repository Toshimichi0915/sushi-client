package net.sushiclient.client.handlers;

import net.minecraft.network.play.client.CPacketPlayer;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.packet.PacketSendEvent;
import net.sushiclient.client.utils.player.PositionPacketUtils;

public class PositionPacketHandler {

    @EventHandler(timing = EventTiming.POST)
    public void onPacketSend(PacketSendEvent e) {
        if (!(e.getPacket() instanceof CPacketPlayer)) return;
        PositionPacketUtils.increment();
    }
}
