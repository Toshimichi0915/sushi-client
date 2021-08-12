package net.sushiclient.client.events.packet;

import net.minecraft.network.Packet;

public class PacketReceiveEvent extends PacketEvent {
    public PacketReceiveEvent(Packet<?> packet) {
        super(packet);
    }
}
