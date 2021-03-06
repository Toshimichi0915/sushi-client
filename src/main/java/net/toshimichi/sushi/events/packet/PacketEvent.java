package net.toshimichi.sushi.events.packet;

import net.minecraft.network.Packet;
import net.toshimichi.sushi.events.CancellableEvent;

public class PacketEvent extends CancellableEvent {

    private final Packet<?> packet;

    public PacketEvent(Packet<?> packet) {
        this.packet = packet;
    }

    public Packet<?> getPacket() {
        return packet;
    }
}
