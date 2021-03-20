package net.toshimichi.sushi.handlers;

import net.toshimichi.sushi.Sushi;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.client.LoadWorldEvent;

public class ConfigurationHandler {

    @EventHandler
    public void onLoadWorld(LoadWorldEvent e) {
        Sushi.getProfile().save();
        System.out.println("saved!");
    }
}
