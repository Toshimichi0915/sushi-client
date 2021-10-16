package net.sushiclient.client.handlers;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.input.KeyPressEvent;
import net.sushiclient.client.events.input.KeyReleaseEvent;
import net.sushiclient.client.events.tick.GameTickEvent;
import org.lwjgl.input.Keyboard;

public class KeyReleaseHandler {

    private final IntArrayList heldKeys = new IntArrayList();

    @EventHandler(timing = EventTiming.PRE, priority = -100)
    public void onKeyPress(KeyPressEvent e) {
        heldKeys.add(e.getKeyCode());
    }

    @EventHandler(timing = EventTiming.PRE, priority = -100)
    public void onKeyRelease(KeyReleaseEvent e) {
        heldKeys.rem(e.getKeyCode());
    }

    @EventHandler(timing = EventTiming.PRE, priority = -100)
    public void onGameTick(GameTickEvent e) {
        for (int keyCode : new IntArrayList(heldKeys)) {
            if (Keyboard.isKeyDown(keyCode)) continue;
            KeyReleaseEvent event = new KeyReleaseEvent(keyCode);
            EventHandlers.callEvent(event);
        }
    }
}
