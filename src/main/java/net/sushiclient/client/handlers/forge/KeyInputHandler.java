package net.sushiclient.client.handlers.forge;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.sushiclient.client.utils.player.InputUtils;

public class KeyInputHandler {

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent e) {
        InputUtils.callKeyEvent();
    }
}
