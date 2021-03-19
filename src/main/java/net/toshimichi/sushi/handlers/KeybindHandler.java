package net.toshimichi.sushi.handlers;

import net.toshimichi.sushi.Sushi;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.input.KeyPressEvent;
import net.toshimichi.sushi.modules.Module;
import net.toshimichi.sushi.modules.Modules;

import java.util.Map;

public class KeybindHandler {

    @EventHandler(priority = 500, timing = EventTiming.POST)
    public void onKeyPress(KeyPressEvent e) {
        for (Module module : Sushi.getProfile().getModules().getAll()) {
            if (module.getKeybind() != e.getKeyCode()) continue;
            module.setEnabled(!module.isEnabled());
            e.setCancelled(true);
        }
    }
}
