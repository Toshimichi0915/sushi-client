package net.toshimichi.sushi.modules.client;

import net.minecraft.client.Minecraft;
import net.toshimichi.sushi.Sushi;
import net.toshimichi.sushi.config.Configurations;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.client.LoadWorldEvent;
import net.toshimichi.sushi.events.tick.ClientTickEvent;
import net.toshimichi.sushi.gui.Components;
import net.toshimichi.sushi.gui.PanelComponent;
import net.toshimichi.sushi.modules.*;
import net.toshimichi.sushi.utils.GuiUtils;
import org.lwjgl.input.Keyboard;

public class ClickGuiModule extends BaseModule {

    private boolean cancelEnable;
    private PanelComponent component;

    public ClickGuiModule(String id, Modules modules, Categories categories, Configurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
    }

    @Override
    public void onEnable() {
        if (Minecraft.getMinecraft().world == null) {
            cancelEnable = true;
            return;
        }
        EventHandlers.register(this);

        component = Sushi.getProfile().getTheme().newClickGui(this);
        Components.show(component, true);
        GuiUtils.lockGame();
    }

    @Override
    public void onDisable() {
        if (cancelEnable) {
            cancelEnable = false;
            return;
        }
        EventHandlers.register(this);

        GuiUtils.unlockGame();
        Minecraft.getMinecraft().setIngameFocus();
        Components.close(component);
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onClientTick(ClientTickEvent e) {
        if (!cancelEnable) return;
        setEnabled(false);
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onLoadWorld(LoadWorldEvent e) {
        setEnabled(false);
    }

    @Override
    public String getDefaultName() {
        return "ClickGUI";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.CLIENT;
    }

    @Override
    public int getDefaultKeybind() {
        return Keyboard.KEY_RSHIFT;
    }
}
