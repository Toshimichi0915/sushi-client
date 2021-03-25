package net.toshimichi.sushi.handlers;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.input.KeyPressEvent;
import net.toshimichi.sushi.events.input.KeyReleaseEvent;
import net.toshimichi.sushi.events.tick.RenderTickEvent;
import org.lwjgl.input.Keyboard;

public class KeyFixHandler {

    private final IntArrayList heldKeys = new IntArrayList();

    @EventHandler(timing = EventTiming.PRE, priority = 100)
    public void onKeyPress(KeyPressEvent e) {
        heldKeys.add(e.getKeyCode());
    }

    @EventHandler(timing = EventTiming.PRE, priority = 100)
    public void onKeyRelease(KeyReleaseEvent e) {
        heldKeys.rem(e.getKeyCode());
    }

    @EventHandler(timing = EventTiming.PRE, priority = 100)
    public void onRenderTick(RenderTickEvent e) {
        for (int keyCode : new IntArrayList(heldKeys)) {
            if (Keyboard.isKeyDown(keyCode)) return;
            KeyReleaseEvent event = new KeyReleaseEvent(keyCode);
            EventHandlers.callEvent(event);
        }
    }
}
