package net.toshimichi.sushi.modules.client;

import net.toshimichi.sushi.gui.clickgui.ClickGuiComponent;
import net.toshimichi.sushi.gui.Components;
import net.toshimichi.sushi.modules.BaseModule;
import net.toshimichi.sushi.modules.Categories;
import net.toshimichi.sushi.modules.Category;
import net.toshimichi.sushi.modules.Modules;
import net.toshimichi.sushi.modules.config.Configurations;
import org.lwjgl.input.Keyboard;

public class ClickGuiModule extends BaseModule {

    private ClickGuiComponent component;

    public ClickGuiModule(String name, Modules modules, Categories categories, Configurations provider) {
        super(name, modules, categories, provider);
    }

    @Override
    public void onEnable() {
        component = new ClickGuiComponent(getConfigurations());
        Components.show(component, true);
    }

    @Override
    public void onDisable() {
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
