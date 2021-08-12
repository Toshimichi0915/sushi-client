package net.sushiclient.client.handlers;

import net.sushiclient.client.Sushi;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.client.WorldLoadEvent;

public class ConfigurationHandler {

    @EventHandler(timing = EventTiming.PRE)
    public void onLoadWorld(WorldLoadEvent e) {
        if (e.getClient() == null) {
            Sushi.getProfile().getModules().disable();
            Sushi.getProfile().save();
        }
    }

    @EventHandler(timing = EventTiming.POST)
    public void onPostWorldLoad(WorldLoadEvent e) {
        if (e.getClient() != null) {
            Sushi.getProfile().getModules().enable();
        }
    }
}
