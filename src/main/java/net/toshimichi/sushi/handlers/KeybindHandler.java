package net.toshimichi.sushi.handlers;

import com.google.common.primitives.Ints;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.client.Minecraft;
import net.toshimichi.sushi.Sushi;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.input.KeyPressEvent;
import net.toshimichi.sushi.events.input.KeyReleaseEvent;
import net.toshimichi.sushi.events.tick.ClientTickEvent;
import net.toshimichi.sushi.modules.ActivationType;
import net.toshimichi.sushi.modules.Module;

import java.util.ArrayList;

public class KeybindHandler {

    private final IntArrayList heldKeys = new IntArrayList();
    private final ArrayList<Module> heldModules = new ArrayList<>();

    private boolean checkKeybind(Module module) {
        if (module.getKeybind().getKeys().length == 0)
            return false;
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
        ArrayList<Module> candidates = new ArrayList<>();
        int maxKeys = 0;
        for (Module module : Sushi.getProfile().getModules().getAll()) {
            if (checkKeybind(module)) {
                candidates.add(module);
                int keys = module.getKeybind().getKeys().length;
                if (keys > maxKeys) maxKeys = keys;
            }
        }
        if (candidates.isEmpty()) return;
        int finalMaxKeys = maxKeys;
        candidates.removeIf(it -> it.getKeybind().getKeys().length < finalMaxKeys);
        for (Module module : candidates) {
            if (heldModules.contains(module) && module.isEnabled()) return;
            heldModules.add(module);
            ActivationType type = module.getKeybind().getActivationType();
            if (type == ActivationType.HOLD)
                module.setEnabled(true);
            else if (type == ActivationType.TOGGLE)
                module.setEnabled(!module.isEnabled());
        }
        e.setCancelled(true);
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onKeyRelease(KeyReleaseEvent e) {
        heldKeys.rem(e.getKeyCode());
        if (!Minecraft.getMinecraft().inGameHasFocus) return;
        for (Module module : new ArrayList<>(heldModules)) {
            if (checkKeybind(module)) continue;
            e.setCancelled(true);
            heldModules.remove(module);
            if (module.getKeybind().getActivationType() == ActivationType.HOLD)
                module.setEnabled(false);
        }
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onClientTick(ClientTickEvent e) {
        if (Minecraft.getMinecraft().inGameHasFocus) return;
        for (Module module : new ArrayList<>(heldModules)) {
            if (module.getKeybind().getActivationType() == ActivationType.HOLD)
                module.setEnabled(false);
        }
        heldModules.clear();
    }
}
