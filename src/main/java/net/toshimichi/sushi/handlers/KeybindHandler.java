package net.toshimichi.sushi.handlers;

import net.toshimichi.sushi.Sushi;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.input.KeyPressEvent;
import net.toshimichi.sushi.modules.Module;
import net.toshimichi.sushi.modules.Modules;

import java.util.Map;

public class KeybindHandler {

    @EventHandler
    public void onKeyPress(KeyPressEvent e) {
        for (Map.Entry<String, Module> entry : Sushi.getProfile().getModules().getAll().entrySet()) {
            Module module = entry.getValue();
            if (module.getKeybind() != e.getKeyCode()) continue;
            module.setEnabled(!module.isEnabled());
            e.setCancelled(true);
        }
    }
}
