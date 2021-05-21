package net.toshimichi.sushi.modules.client;

import net.minecraft.client.Minecraft;
import net.toshimichi.sushi.Sushi;
import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.Configurations;
import net.toshimichi.sushi.gui.ComponentContext;
import net.toshimichi.sushi.gui.Components;
import net.toshimichi.sushi.gui.PanelComponent;
import net.toshimichi.sushi.gui.theme.Theme;
import net.toshimichi.sushi.modules.*;
import net.toshimichi.sushi.utils.GuiUtils;
import org.lwjgl.input.Keyboard;

public class ClickGuiModule extends BaseModule {

    private final Theme fallbackTheme;
    private final Configuration<String> theme;
    private ComponentContext<PanelComponent<? extends net.toshimichi.sushi.gui.Component>> context;

    public ClickGuiModule(String id, Modules modules, Categories categories, Configurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        fallbackTheme = Sushi.getDefaultTheme();
        theme = provider.get("theme", "Theme", "ClickGUI Theme", String.class, fallbackTheme.getId());
    }

    @Override
    protected boolean isTemporaryByDefault() {
        return true;
    }

    @Override
    public void onEnable() {
        Theme theme = fallbackTheme;
        for (Theme t : Sushi.getThemes()) {
            if (t.getId().equalsIgnoreCase(this.theme.getId())) {
                theme = t;
                break;
            }
        }
        PanelComponent<?> component = theme.newClickGui(this);
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
