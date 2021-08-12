package net.sushiclient.client.events.packet;

import net.minecraft.network.Packet;
import net.sushiclient.client.events.CancellableEvent;

public class PacketEvent extends CancellableEvent {

    private Packet<?> packet;

    public PacketEvent(Packet<?> packet) {
        this.packet = packet;
    }

    public Packet<?> getPacket() {
        return packet;
    }

    public void setPacket(Packet<?> packet) {
        this.packet = packet;
    }
}
