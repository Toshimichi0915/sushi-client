package net.toshimichi.sushi.handlers;

import net.toshimichi.sushi.Sushi;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.input.KeyPressEvent;
import net.toshimichi.sushi.modules.Module;

public class KeybindHandler {

    @EventHandler(timing = EventTiming.PRE, priority = 1500)
    public void onKeyPress(KeyPressEvent e) {
        for (Module module : Sushi.getProfile().getModules().getAll()) {
            if (module.getKeybind() != e.getKeyCode()) continue;
            module.setEnabled(!module.isEnabled());
            e.setCancelled(true);
        }
    }
}
