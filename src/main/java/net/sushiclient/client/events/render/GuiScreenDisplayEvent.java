package net.sushiclient.client.events.render;

import net.minecraft.client.gui.GuiScreen;
import net.sushiclient.client.events.BaseEvent;
import net.sushiclient.client.events.EventTiming;

public class GuiScreenDisplayEvent extends BaseEvent {

    private GuiScreen guiScreen;

    public GuiScreenDisplayEvent(GuiScreen guiScreen, EventTiming timing) {
        super(timing);
        this.guiScreen = guiScreen;
    }

    public GuiScreen getGuiScreen() {
        return guiScreen;
    }

    public void setGuiScreen(GuiScreen guiScreen) {
        this.guiScreen = guiScreen;
    }
}
