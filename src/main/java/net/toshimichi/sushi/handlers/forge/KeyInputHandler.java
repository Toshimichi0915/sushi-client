package net.toshimichi.sushi.handlers.forge;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.toshimichi.sushi.events.CancellableEvent;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.input.KeyPressEvent;
import net.toshimichi.sushi.events.input.KeyReleaseEvent;
import org.lwjgl.input.Keyboard;

public class KeyInputHandler {

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent e) {
        if(Keyboard.isRepeatEvent()) return;
        int eventKey = Keyboard.getEventKey();
        CancellableEvent event;
        if (Keyboard.isKeyDown(eventKey))
            event = new KeyPressEvent(eventKey);
        else
            event = new KeyReleaseEvent(eventKey);
        EventHandlers.callEvent(event);
    }
}
