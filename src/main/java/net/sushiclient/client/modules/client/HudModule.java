package net.sushiclient.client.modules.client;

import net.sushiclient.client.Sushi;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.gui.Components;
import net.sushiclient.client.gui.hud.HudComponent;
import net.sushiclient.client.gui.hud.HudEditComponent;
import net.sushiclient.client.gui.theme.Theme;
import net.sushiclient.client.modules.*;

public class HudModule extends BaseModule {
    private final Theme fallbackTheme;
    private final HudComponent hudComponent;
    private final Configuration<String> theme;

    public HudModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        hudComponent = newHudComponent(provider);
        fallbackTheme = Sushi.getDefaultTheme();
        theme = provider.get("theme", "Theme", "HUD Theme", String.class, fallbackTheme.getId());
        provider.temp("editor", "Open Editor", null, Runnable.class, () -> {
            Components.closeAll();
            setEnabled(true);
            Theme theme = fallbackTheme;
            for (Theme t : Sushi.getThemes()) {
                if (t.getId().equalsIgnoreCase(this.theme.getId())) {
                    theme = t;
                    break;
                }
            }
            Components.show(new HudEditComponent(theme, hudComponent), false, false);
        });
    }

    protected HudComponent newHudComponent(RootConfigurations configurations) {
        return new HudComponent(configurations, this);
    }

    @Override
    public void onEnable() {
        Components.show(hudComponent, true, false, Components.getAll().size());
    }

    @Override
    public void onDisable() {
        hudComponent.getContext().close();
    }

    @Override
    public String getDefaultName() {
        return "HUD";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.CLIENT;
    }
}
