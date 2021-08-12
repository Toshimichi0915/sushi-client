package net.sushiclient.client.handlers.forge;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.tick.ClientTickEvent;

public class ClientTickHandler {

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e) {
        EventTiming timing;
        if (e.phase == TickEvent.Phase.START)
            timing = EventTiming.PRE;
        else
            timing = EventTiming.POST;

        ClientTickEvent event = new ClientTickEvent(timing);
        EventHandlers.callEvent(event);
    }
}
