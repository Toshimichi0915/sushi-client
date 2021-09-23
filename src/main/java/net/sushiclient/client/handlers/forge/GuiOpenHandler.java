package net.sushiclient.client.handlers.forge;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.render.GuiScreenDisplayEvent;

public class GuiOpenHandler {

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent e) {
        GuiScreen screen = e.getGui();
        GuiScreenDisplayEvent event = new GuiScreenDisplayEvent(screen, EventTiming.PRE);
        EventHandlers.callEvent(event);
        e.setGui(event.getGuiScreen());
    }
}
