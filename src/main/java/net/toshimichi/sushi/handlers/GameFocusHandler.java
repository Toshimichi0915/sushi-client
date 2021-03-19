package net.toshimichi.sushi.handlers;

import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.client.GameFocusEvent;
import net.toshimichi.sushi.utils.GuiUtils;

public class GameFocusHandler {

    @EventHandler(timing = EventTiming.PRE)
    public void onFocus(GameFocusEvent e) {
        if (e.isFocused() && GuiUtils.isGameLocked()) {
            e.setCancelled(true);
        }
    }
}
