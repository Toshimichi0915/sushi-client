package net.sushiclient.client.modules.render;

import net.sushiclient.client.Sushi;
import net.sushiclient.client.config.Config;
import net.sushiclient.client.config.ConfigInjector;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.config.data.EspColor;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.world.WorldRenderEvent;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.modules.combat.HoleMineInfo;
import net.sushiclient.client.modules.combat.HoleMinerModule;

import java.awt.Color;

public class HoleMinerHelperModule extends BaseModule {

    @Config(id = "hole_miner_id", name = "Hole Miner ID")
    public String holeMinerId = "hole_miner";

    @Config(id = "render_mode", name = "Render Mode")
    public RenderMode renderMode = RenderMode.FULL;

    @Config(id = "color", name = "Color")
    public EspColor color = new EspColor(new Color(255, 0, 0, 50), false, true);

    public HoleMinerHelperModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
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

    @EventHandler(timing = EventTiming.POST)
    public void onWorldRender(WorldRenderEvent e) {
        Module module = Sushi.getProfile().getModules().getModule(holeMinerId);
        if (!(module instanceof HoleMinerModule)) return;
        HoleMinerModule holeMiner = (HoleMinerModule) module;
        holeMiner.updateHoleMineInfo();
        HoleMineInfo holeMineInfo = holeMiner.getHoleMineInfo();
        if (holeMineInfo == null) return;
        holeMineInfo.render(getWorld(), renderMode, color);
    }

    @Override
    public String getDefaultName() {
        return "HoleMinerHelper";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.RENDER;
    }
}
