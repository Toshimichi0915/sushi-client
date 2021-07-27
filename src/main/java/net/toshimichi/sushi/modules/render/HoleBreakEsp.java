package net.toshimichi.sushi.modules.render;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.toshimichi.sushi.config.Config;
import net.toshimichi.sushi.config.ConfigInjector;
import net.toshimichi.sushi.config.RootConfigurations;
import net.toshimichi.sushi.config.data.EspColor;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.world.WorldRenderEvent;
import net.toshimichi.sushi.modules.*;
import net.toshimichi.sushi.modules.combat.HoleMineInfo;
import net.toshimichi.sushi.utils.EntityUtils;
import net.toshimichi.sushi.utils.render.hole.HoleUtils;

import java.awt.Color;

public class HoleBreakEsp extends BaseModule {

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
            for(EnumFacing facing: EnumFacing.HORIZONTALS) {
                HoleMineInfo info = HoleUtils.findNormal(player, facing);
                if(info == null) continue;
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
}
