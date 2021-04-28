package net.toshimichi.sushi.handlers;

import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.tick.OverlayRenderEvent;
import net.toshimichi.sushi.gui.Component;
import net.toshimichi.sushi.gui.ComponentContext;
import net.toshimichi.sushi.gui.Components;

import java.util.Collections;
import java.util.List;

public class ComponentRenderHandler {

    @EventHandler(timing = EventTiming.POST)
    public void onOverlayRender(OverlayRenderEvent e) {
        List<ComponentContext<?>> components = Components.getAll();
        Collections.reverse(components);
        for (ComponentContext<?> component : components) {
            Component origin = component.getOrigin();
            if (origin != null && component.getOrigin().isVisible()) {
                origin.onRelocate();
                origin.onRender();
            }
        }
    }
}
