package net.toshimichi.sushi.events;

import net.minecraft.network.Packet;

public class PacketReceiveEvent extends PacketEvent {
    public PacketReceiveEvent(Packet<?> packet) {
        super(packet);
    }
}
