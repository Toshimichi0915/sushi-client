package net.toshimichi.sushi.handlers;

import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.input.KeyPressEvent;
import net.toshimichi.sushi.events.input.KeyReleaseEvent;
import net.toshimichi.sushi.gui.ComponentContext;
import net.toshimichi.sushi.gui.Components;

public class ComponentKeyHandler {

    @EventHandler(timing = EventTiming.PRE)
    public void onKeyPress(KeyPressEvent e) {
        ComponentContext<?> topComponent = Components.getTopContext();
        if (topComponent == null) return;
        topComponent.getOrigin().onKeyPressed(e.getKeyCode(), e.getKey());
        e.setCancelled(true);
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onKeyRelease(KeyReleaseEvent e) {
        ComponentContext<?> topComponent = Components.getTopContext();
        if (topComponent == null) return;
        topComponent.getOrigin().onKeyReleased(e.getKeyCode());
        e.setCancelled(true);
    }
}
