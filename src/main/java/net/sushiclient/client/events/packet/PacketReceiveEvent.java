package net.sushiclient.client.events.packet;

import net.minecraft.network.Packet;
import net.sushiclient.client.events.EventTiming;

public class PacketReceiveEvent extends PacketEvent {
    public PacketReceiveEvent(EventTiming timing, Packet<?> packet) {
        super(timing, packet);
    }
}
