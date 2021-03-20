package net.toshimichi.sushi.utils;

import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.input.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class InputUtils {
    public static KeyEvent callKeyEvent() {
        if (Keyboard.isRepeatEvent()) return null;
        int eventKey = Keyboard.getEventKey();
        KeyEvent event;
        if (Keyboard.isKeyDown(eventKey)) event = new KeyPressEvent(eventKey);
        else event = new KeyReleaseEvent(eventKey);
        EventHandlers.callEvent(event);
        return event;
    }

    public static MouseEvent callMouseEvent() {
        if (Mouse.getEventButton() == -1) return null;
        int mouse = Mouse.getEventButton();
        ClickType clickType;
        if (mouse == 0) clickType = ClickType.LEFT;
        else clickType = ClickType.RIGHT;
        MouseEvent event;
        if (Mouse.isButtonDown(mouse)) event = new MousePressEvent(clickType);
        else event = new MouseReleaseEvent(clickType);

        EventHandlers.callEvent(event);
        return event;
    }
}
