package net.toshimichi.sushi.handlers.forge;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.toshimichi.sushi.events.CancellableEvent;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.input.ClickType;
import net.toshimichi.sushi.events.input.MousePressEvent;
import net.toshimichi.sushi.events.input.MouseReleaseEvent;
import org.lwjgl.input.Mouse;


public class MouseInputHandler {

    @SubscribeEvent
    public void onMouseInput(InputEvent.MouseInputEvent e) {
        if (!Mouse.getEventButtonState()) return;
        int mouse = Mouse.getEventButton();
        ClickType clickType;
        if (mouse == 0) clickType = ClickType.LEFT;
        else clickType = ClickType.RIGHT;
        CancellableEvent event;
        if (Mouse.isButtonDown(mouse))
            event = new MousePressEvent(clickType);
        else
            event = new MouseReleaseEvent(clickType);

        EventHandlers.callEvent(event);
    }
}
