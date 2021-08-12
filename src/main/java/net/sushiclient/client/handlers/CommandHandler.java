package net.sushiclient.client.handlers;

import net.minecraft.network.play.client.CPacketChatMessage;
import net.sushiclient.client.Sushi;
import net.sushiclient.client.command.Commands;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.packet.PacketSendEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandHandler {

    @EventHandler(timing = EventTiming.PRE)
    public void onChatSend(PacketSendEvent e) {
        if (!(e.getPacket() instanceof CPacketChatMessage)) return;
        String message = ((CPacketChatMessage) e.getPacket()).getMessage();
        if (message.isEmpty()) return;
        if (message.charAt(0) != Sushi.getProfile().getPrefix()) return;
        e.setCancelled(true);
        List<String> list = Arrays.asList(message.substring(1).split("\\s+"));
        List<String> args = list.size() > 1 ? list.subList(1, list.size()) : Collections.emptyList();
        Commands.execute(Sushi.getProfile().getLogger(), list.get(0), args);
    }
}
