package net.toshimichi.sushi.events.forge;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.toshimichi.sushi.events.CancellableEvent;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.KeyPressEvent;
import net.toshimichi.sushi.events.KeyReleaseEvent;
import org.lwjgl.input.Keyboard;

public class KeyInputHandler {

    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent e) {
        if (!Keyboard.getEventKeyState()) return;
        int eventKey = Keyboard.getEventKey();
        CancellableEvent event;
        if (Keyboard.isKeyDown(eventKey))
            event = new KeyPressEvent(eventKey);
        else
            event = new KeyReleaseEvent(eventKey);
        EventHandlers.callEvent(event);
        e.setCanceled(event.isCancelled());
    }
}
