package net.sushiclient.client.events.packet;

import net.minecraft.network.Packet;
import net.sushiclient.client.events.EventTiming;

public class PacketSendEvent extends PacketEvent {
    public PacketSendEvent(EventTiming timing, Packet<?> packet) {
        super(timing, packet);
    }
}
