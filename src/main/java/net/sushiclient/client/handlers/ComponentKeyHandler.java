package net.sushiclient.client.handlers;

import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.input.KeyPressEvent;
import net.sushiclient.client.events.input.KeyReleaseEvent;
import net.sushiclient.client.gui.ComponentContext;
import net.sushiclient.client.gui.Components;
import net.sushiclient.client.utils.render.GuiUtils;

public class ComponentKeyHandler {

    @EventHandler(timing = EventTiming.PRE, priority = 1500)
    public void onKeyPress(KeyPressEvent e) {
        if (!GuiUtils.isGameLocked()) return;
        ComponentContext<?> topComponent = Components.getTopContext();
        if (topComponent == null) return;
        topComponent.getOrigin().onKeyPressed(e.getKeyCode(), e.getKey());
    }

    @EventHandler(timing = EventTiming.PRE, priority = 1500)
    public void onKeyRelease(KeyReleaseEvent e) {
        if (!GuiUtils.isGameLocked()) return;
        ComponentContext<?> topComponent = Components.getTopContext();
        if (topComponent == null) return;
        topComponent.getOrigin().onKeyReleased(e.getKeyCode());
    }
}
