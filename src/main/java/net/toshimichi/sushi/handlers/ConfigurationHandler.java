package net.toshimichi.sushi.handlers;

import net.toshimichi.sushi.Sushi;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.client.LoadWorldEvent;

public class ConfigurationHandler {

    @EventHandler(timing = EventTiming.PRE)
    public void onLoadWorld(LoadWorldEvent e) {
        if (e.getClient() == null) {
            Sushi.getProfile().save();
        }
    }
}
