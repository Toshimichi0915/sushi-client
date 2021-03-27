package net.toshimichi.sushi.handlers;

import net.toshimichi.sushi.Sushi;
import net.toshimichi.sushi.command.Commands;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.client.ChatSendEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandHandler {

    @EventHandler(timing = EventTiming.PRE)
    public void onChatSend(ChatSendEvent e) {
        String message = e.getMessage();
        if (message.isEmpty()) return;
        if (message.charAt(0) != Sushi.getProfile().getPrefix()) return;
        e.setCancelled(true);
        List<String> list = Arrays.asList(message.substring(1).split("\\s+"));
        List<String> args = list.size() > 1 ? list.subList(1, list.size()) : Collections.emptyList();
        Commands.execute(Sushi.getProfile().getMessageHandler(), list.get(0), args);
    }
}
