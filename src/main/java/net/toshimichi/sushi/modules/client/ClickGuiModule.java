package net.toshimichi.sushi.modules.client;

import net.minecraft.client.Minecraft;
import net.toshimichi.sushi.Sushi;
import net.toshimichi.sushi.gui.Components;
import net.toshimichi.sushi.gui.PanelComponent;
import net.toshimichi.sushi.modules.BaseModule;
import net.toshimichi.sushi.modules.Categories;
import net.toshimichi.sushi.modules.Category;
import net.toshimichi.sushi.modules.Modules;
import net.toshimichi.sushi.modules.config.Configurations;
import net.toshimichi.sushi.utils.GuiUtils;
import org.lwjgl.input.Keyboard;

public class ClickGuiModule extends BaseModule {

    private PanelComponent component;

    public ClickGuiModule(String id, String name, Modules modules, Categories categories, Configurations provider) {
        super(id, name, modules, categories, provider);
    }

    @Override
    public void onEnable() {
        GuiUtils.lockGame();
        component = Sushi.getProfile().getTheme().newClickGui();
        Components.show(component, true);
    }

    @Override
    public void onDisable() {
        GuiUtils.unlockGame();
        Minecraft.getMinecraft().setIngameFocus();
        Components.close(component);
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
