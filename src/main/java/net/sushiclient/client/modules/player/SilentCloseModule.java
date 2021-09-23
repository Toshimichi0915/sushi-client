package net.sushiclient.client.modules.player;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.network.play.server.SPacketCloseWindow;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.packet.PacketReceiveEvent;
import net.sushiclient.client.events.packet.PacketSendEvent;
import net.sushiclient.client.events.render.GuiScreenCloseEvent;
import net.sushiclient.client.events.render.GuiScreenDisplayEvent;
import net.sushiclient.client.modules.*;

public class SilentCloseModule extends BaseModule {

    private GuiScreen container;

    public SilentCloseModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
    }

    @Override
    public void onEnable() {
        container = null;
        EventHandlers.register(this);
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onDisplayGuiScreen(GuiScreenDisplayEvent event) {
        if (!((event.getGuiScreen()) instanceof GuiInventory)) return;
        if (container == null) return;
        event.setGuiScreen(container);
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onCloseGuiScreen(GuiScreenCloseEvent event) {
        if (!((event.getGuiScreen()) instanceof GuiContainer)) return;
        event.setCancelled(true);
        container = event.getGuiScreen();
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onPacketSend(PacketSendEvent e) {
        if (!(e.getPacket() instanceof CPacketCloseWindow)) return;
        e.setCancelled(true);
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onPacketReceive(PacketReceiveEvent e) {
        if (!(e.getPacket() instanceof SPacketCloseWindow)) return;
        getClient().addScheduledTask(() -> {
            container = null;
            if (getClient().currentScreen == container) {
                getPlayer().closeScreen();
            }
        });
    }

    @Override
    public String getDefaultName() {
        return "SilentClose";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.PLAYER;
    }
}
