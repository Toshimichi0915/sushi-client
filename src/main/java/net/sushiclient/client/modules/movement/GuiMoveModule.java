package net.sushiclient.client.modules.movement;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.sushiclient.client.config.Config;
import net.sushiclient.client.config.ConfigInjector;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.input.KeyDownCheckEvent;
import net.sushiclient.client.events.player.PlayerTickEvent;
import net.sushiclient.client.modules.*;
import org.lwjgl.input.Keyboard;

public class GuiMoveModule extends BaseModule {

    @Config(id = "sneak", name = "Sneak")
    public Boolean sneak = true;

    public GuiMoveModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        new ConfigInjector(provider).inject(this);
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onUpdate(PlayerTickEvent e) {
        GuiScreen screen = getClient().currentScreen;
        if (screen == null) return;
        if (screen instanceof GuiChat || screen instanceof GuiEditSign || screen instanceof GuiRepair) return;
        GameSettings settings = getClient().gameSettings;
        KeyBinding[] keys = new KeyBinding[]{settings.keyBindForward, settings.keyBindBack,
                settings.keyBindLeft, settings.keyBindRight, settings.keyBindJump, settings.keyBindSneak};
        for (int i = 0; i < (sneak ? keys.length : keys.length - 1); i++) {
            KeyBinding bind = keys[i];
            KeyBinding.setKeyBindState(bind.getKeyCode(), Keyboard.isKeyDown(bind.getKeyCode()));
        }
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onKeyDownCheck(KeyDownCheckEvent e) {
        e.setResult(e.isPressed());
    }

    @Override
    public String getDefaultName() {
        return "GuiMove";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.MOVEMENT;
    }
}
