package net.toshimichi.sushi.utils.player;

import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.input.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class InputUtils {
    public static void callKeyEvent() {
        if (Keyboard.isRepeatEvent()) return;
        int eventKey = Keyboard.getEventKey();
        if (!Keyboard.isKeyDown(eventKey)) return;
        if (eventKey == 0) return;
        KeyPressEvent event = new KeyPressEvent(eventKey, Keyboard.getEventCharacter());
        EventHandlers.callEvent(event);
    }

    public static void callMouseEvent() {
        if (Mouse.getEventButton() == -1) return;
        int mouse = Mouse.getEventButton();
        ClickType clickType;
        if (mouse == 0) clickType = ClickType.LEFT;
        else clickType = ClickType.RIGHT;
        MouseEvent event;
        if (Mouse.isButtonDown(mouse)) event = new MousePressEvent(clickType);
        else event = new MouseReleaseEvent(clickType);
        EventHandlers.callEvent(event);
    }
}
