package net.toshimichi.sushi.handlers;

import net.toshimichi.sushi.SushiMod;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.input.KeyPressEvent;
import net.toshimichi.sushi.modules.Module;

public class KeybindHandler {

    @EventHandler
    public void onKeyPress(KeyPressEvent e) {
        for (Module module : SushiMod.getModules().getModules()) {
            if (module.getKeybind() != e.getKeyCode()) continue;
            module.setEnabled(!module.isEnabled());
            e.setCancelled(true);
        }
    }
}
