package net.toshimichi.sushi.handlers;

import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.input.KeyPressEvent;
import net.toshimichi.sushi.events.input.KeyReleaseEvent;
import net.toshimichi.sushi.gui.Component;
import net.toshimichi.sushi.gui.Components;

public class ComponentKeyHandler {

    @EventHandler(timing = EventTiming.PRE)
    public void onKeyPress(KeyPressEvent e) {
        Component topComponent = Components.getTopComponent(true);
        if (topComponent == null) return;
        topComponent.onKeyPressed(e.getKeyCode());
        e.setCancelled(true);
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onKeyRelease(KeyReleaseEvent e) {
        Component topComponent = Components.getTopComponent(true);
        if (topComponent == null) return;
        topComponent.onKeyReleased(e.getKeyCode());
        e.setCancelled(true);
    }
}
