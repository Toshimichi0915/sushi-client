package net.sushiclient.client.events.packet;

import net.minecraft.network.Packet;

public class PacketSendEvent extends PacketEvent {
    public PacketSendEvent(Packet<?> packet) {
        super(packet);
    }
}
