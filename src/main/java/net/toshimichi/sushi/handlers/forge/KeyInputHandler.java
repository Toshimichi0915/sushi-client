package net.toshimichi.sushi.handlers.forge;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.toshimichi.sushi.utils.player.InputUtils;

public class KeyInputHandler {

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent e) {
        InputUtils.callKeyEvent();
    }
}
