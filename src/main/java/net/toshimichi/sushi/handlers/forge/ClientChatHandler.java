package net.toshimichi.sushi.handlers.forge;

import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.client.ChatSendEvent;

public class ClientChatHandler {

    @SubscribeEvent
    public void onChat(ClientChatEvent e) {
        ChatSendEvent event = new ChatSendEvent(e.getMessage());
        EventHandlers.callEvent(event);
        e.setCanceled(event.isCancelled());
    }
}
