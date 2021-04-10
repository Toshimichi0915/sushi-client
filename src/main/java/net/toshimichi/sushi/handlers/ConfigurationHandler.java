package net.toshimichi.sushi.handlers;

import net.toshimichi.sushi.Sushi;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.client.WorldLoadEvent;

public class ConfigurationHandler {

    @EventHandler(timing = EventTiming.PRE)
    public void onLoadWorld(WorldLoadEvent e) {
        if (e.getClient() == null) {
            Sushi.getProfile().getModules().disable();
            Sushi.getProfile().save();
        } else {
            Sushi.getProfile().getModules().enable();
        }
    }
}
