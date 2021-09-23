package net.sushiclient.client.events.render;

import net.minecraft.client.gui.GuiScreen;
import net.sushiclient.client.events.CancellableEvent;
import net.sushiclient.client.events.EventTiming;

public class GuiScreenCloseEvent extends CancellableEvent {

    private final GuiScreen guiScreen;

    public GuiScreenCloseEvent(GuiScreen guiScreen, EventTiming timing) {
        super(timing);
        this.guiScreen = guiScreen;
    }

    public GuiScreen getGuiScreen() {
        return guiScreen;
    }
}
