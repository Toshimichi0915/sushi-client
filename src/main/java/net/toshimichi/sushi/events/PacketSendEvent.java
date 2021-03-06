package net.toshimichi.sushi.events;

import net.minecraft.network.Packet;

public class PacketSendEvent extends PacketEvent{
    public PacketSendEvent(Packet<?> packet) {
        super(packet);
    }
}
