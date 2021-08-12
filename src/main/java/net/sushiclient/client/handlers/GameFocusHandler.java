package net.sushiclient.client.handlers;

import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.client.GameFocusEvent;
import net.sushiclient.client.utils.render.GuiUtils;

public class GameFocusHandler {

    @EventHandler(timing = EventTiming.PRE)
    public void onFocus(GameFocusEvent e) {
        if (e.isFocused() && GuiUtils.isGameLocked()) {
            e.setCancelled(true);
        }
    }
}
