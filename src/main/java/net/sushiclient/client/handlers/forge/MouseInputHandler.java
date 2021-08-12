package net.sushiclient.client.handlers.forge;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.sushiclient.client.utils.player.InputUtils;

public class MouseInputHandler {

    @SubscribeEvent
    public void onMouseInput(InputEvent.MouseInputEvent e) {
        InputUtils.callMouseEvent();
    }
}
