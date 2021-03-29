package net.toshimichi.sushi.handlers;

import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.input.KeyPressEvent;
import net.toshimichi.sushi.events.input.KeyReleaseEvent;
import net.toshimichi.sushi.gui.ComponentContext;
import net.toshimichi.sushi.gui.Components;
import net.toshimichi.sushi.utils.GuiUtils;

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
