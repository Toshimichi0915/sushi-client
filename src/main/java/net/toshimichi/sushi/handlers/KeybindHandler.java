package net.toshimichi.sushi.handlers;

import com.google.common.primitives.Ints;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.client.Minecraft;
import net.toshimichi.sushi.Sushi;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.input.KeyPressEvent;
import net.toshimichi.sushi.events.input.KeyReleaseEvent;
import net.toshimichi.sushi.modules.ActivationType;
import net.toshimichi.sushi.modules.Module;

import java.util.ArrayList;

public class KeybindHandler {

    private final IntArrayList heldKeys = new IntArrayList();
    private final ArrayList<Module> heldModules = new ArrayList<>();

    private boolean checkKeybind(Module module) {
        int[] keys = heldKeys.toIntArray();
        for (int key : module.getKeybind().getKeys()) {
            if (!Ints.contains(keys, key)) {
                return false;
            }
        }
        return true;
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onKeyPress(KeyPressEvent e) {
        heldKeys.add(e.getKeyCode());
        if (!Minecraft.getMinecraft().inGameHasFocus) return;
        for (Module module : Sushi.getProfile().getModules().getAll()) {
            if (!checkKeybind(module) || heldModules.contains(module))
                continue;
            heldModules.add(module);
            ActivationType type = module.getKeybind().getActivationType();
            if (type == ActivationType.HOLD)
                module.setEnabled(true);
            else if (type == ActivationType.TOGGLE)
                module.setEnabled(!module.isEnabled());
            e.setCancelled(true);
        }
    }

    @EventHandler(timing = EventTiming.PRE, priority = 1500)
    public void onKeyRelease(KeyReleaseEvent e) {
        heldKeys.rem(e.getKeyCode());
        for (Module module : new ArrayList<>(heldModules)) {
            if (checkKeybind(module)) continue;
            e.setCancelled(true);
            heldModules.remove(module);
            if (module.getKeybind().getActivationType() == ActivationType.HOLD)
                module.setEnabled(false);
        }
    }
}
