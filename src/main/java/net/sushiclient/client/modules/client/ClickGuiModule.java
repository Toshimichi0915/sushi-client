package net.sushiclient.client.modules.client;

import net.minecraft.client.Minecraft;
import net.sushiclient.annotations.Protect;
import net.sushiclient.client.Sushi;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.gui.Component;
import net.sushiclient.client.gui.ComponentContext;
import net.sushiclient.client.gui.Components;
import net.sushiclient.client.gui.theme.Theme;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.utils.render.GuiUtils;
import org.lwjgl.input.Keyboard;

public class ClickGuiModule extends BaseModule {

    private final Theme fallbackTheme;
    private final Configuration<String> theme;
    private ComponentContext<Component> context;

    public ClickGuiModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        fallbackTheme = Sushi.getDefaultTheme();
        theme = provider.get("theme", "Theme", "ClickGUI Theme", String.class, fallbackTheme.getId());
        // prepare font for SimpleClickGui (for performance reasons)
        // maybe ugly but this should not cause bugs
        getTheme().newClickGui(this);
    }

    @Override
    protected boolean isTemporaryByDefault() {
        return true;
    }

    private Theme getTheme() {
        for (Theme t : Sushi.getThemes()) {
            if (t.getId().equalsIgnoreCase(this.theme.getId())) {
                return t;
            }
        }
        return fallbackTheme;
    }

    @Protect
    @Override
    public void onEnable() {
        Component component = getTheme().newClickGui(this);
        context = Components.show(component, false, false);
        GuiUtils.lockGame(() -> setEnabled(false));
    }

    @Override
    public void onDisable() {
        GuiUtils.unlockGame();
        Minecraft.getMinecraft().setIngameFocus();
        context.close();
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
    public Keybind getDefaultKeybind() {
        return new Keybind(ActivationType.TOGGLE, Keyboard.KEY_RSHIFT);
    }
}
