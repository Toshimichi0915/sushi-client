package net.toshimichi.sushi.events;

import net.minecraft.network.Packet;

public class PacketEvent extends CancellableEvent {

    private final Packet<?> packet;

    public PacketEvent(Packet<?> packet) {
        this.packet = packet;
    }

    public Packet<?> getPacket() {
        return packet;
    }
}
