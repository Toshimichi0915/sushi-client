package net.sushiclient.client.handlers;

import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.render.GuiRenderEvent;
import net.sushiclient.client.events.render.OverlayRenderEvent;
import net.sushiclient.client.gui.Component;
import net.sushiclient.client.gui.ComponentContext;
import net.sushiclient.client.gui.Components;

import java.util.Collections;
import java.util.List;

public class ComponentRenderHandler {

    public void render(boolean overlay) {
        List<ComponentContext<?>> components = Components.getAll();
        Collections.reverse(components);
        for (ComponentContext<?> component : components) {
            if (component.isOverlay() != overlay) continue;
            Component origin = component.getOrigin();
            if (origin != null && component.getOrigin().isVisible()) {
                origin.onRelocate();
                origin.onRender();
            }
        }
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onGuiRender(GuiRenderEvent e) {
        render(false);
    }

    @EventHandler(timing = EventTiming.POST)
    public void onOverlayRender(OverlayRenderEvent e) {
        render(true);
    }
}
