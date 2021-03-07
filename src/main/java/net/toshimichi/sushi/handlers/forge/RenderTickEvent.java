package net.toshimichi.sushi.handlers.forge;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.toshimichi.sushi.events.tick.ClientTickEvent;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;

public class RenderTickEvent {

    @SubscribeEvent
    public void onTick(TickEvent.RenderTickEvent e) {
        EventTiming timing;
        if (e.phase == TickEvent.Phase.START)
            timing = EventTiming.PRE;
        else
            timing = EventTiming.POST;

        ClientTickEvent event = new ClientTickEvent(timing);
        EventHandlers.callEvent(event);
    }
}
