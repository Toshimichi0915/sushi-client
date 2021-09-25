package net.sushiclient.client.modules.render;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
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
import net.sushiclient.client.utils.EntityUtils;
import net.sushiclient.client.utils.render.hole.HoleUtils;

import java.awt.Color;

public class HoleBreakEsp extends BaseModule implements ModuleSuffix {

    @Config(id = "render_mode", name = "Render Mode")
    public RenderMode renderMode = RenderMode.FULL;

    @Config(id = "color", name = "Color")
    public EspColor color = new EspColor(new Color(255, 0, 0, 50), false, true);

    public HoleBreakEsp(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
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
        for (EntityPlayer player : EntityUtils.getNearbyPlayers(6)) {
            for (EnumFacing facing : EnumFacing.HORIZONTALS) {
                HoleMineInfo info = HoleUtils.findNormal(player, facing);
                if (info == null) continue;
                info.render(getWorld(), renderMode, color);
            }
        }
    }

    @Override
    public String getDefaultName() {
        return "HoleBreakESP";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.RENDER;
    }

    @Override
    public String getSuffix() {
        return renderMode.getName();
    }
}
