package net.toshimichi.sushi.modules.client;

import net.toshimichi.sushi.config.Configurations;
import net.toshimichi.sushi.gui.Components;
import net.toshimichi.sushi.gui.hud.HudComponent;
import net.toshimichi.sushi.modules.*;

public class HudModule extends BaseModule {

    private final HudComponent hudComponent;

    public HudModule(String id, Modules modules, Categories categories, Configurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        this.hudComponent = newHudComponent(provider);
    }

    protected HudComponent newHudComponent(Configurations configurations) {
        return new HudComponent(configurations);
    }

    @Override
    public void onEnable() {
        Components.show(hudComponent, false, Components.getAll().size());
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
