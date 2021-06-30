package net.toshimichi.sushi.handlers;

import net.minecraft.network.play.client.CPacketChatMessage;
import net.toshimichi.sushi.Sushi;
import net.toshimichi.sushi.command.Commands;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.packet.PacketSendEvent;
import net.toshimichi.sushi.hwid.annotations.AsyncAuthentication;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandHandler {

    @AsyncAuthentication
    @EventHandler(timing = EventTiming.PRE)
    public void onChatSend(PacketSendEvent e) {
        if (!(e.getPacket() instanceof CPacketChatMessage)) return;
        String message = ((CPacketChatMessage) e.getPacket()).getMessage();
        if (message.isEmpty()) return;
        if (message.charAt(0) != Sushi.getProfile().getPrefix()) return;
        e.setCancelled(true);
        List<String> list = Arrays.asList(message.substring(1).split("\\s+"));
        List<String> args = list.size() > 1 ? list.subList(1, list.size()) : Collections.emptyList();
        Commands.execute(Sushi.getProfile().getMessageHandler(), list.get(0), args);
    }
}
