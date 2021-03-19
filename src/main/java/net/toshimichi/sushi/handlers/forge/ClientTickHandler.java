package net.toshimichi.sushi.handlers.forge;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.tick.ClientTickEvent;

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
