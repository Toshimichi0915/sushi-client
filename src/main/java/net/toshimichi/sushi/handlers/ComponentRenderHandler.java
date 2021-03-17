package net.toshimichi.sushi.handlers;

import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.tick.RenderTickEvent;
import net.toshimichi.sushi.gui.Component;
import net.toshimichi.sushi.gui.Components;

import java.util.Collections;
import java.util.List;

public class ComponentRenderHandler {

    @EventHandler(timing = {EventTiming.POST})
    public void onRenderTick(RenderTickEvent e) {
        List<Component> components = Components.getAll();
        Collections.reverse(components);
        for(Component component : components) {
            component.onRender();
        }
    }
}
