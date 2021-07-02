package net.toshimichi.sushi.modules.render;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.toshimichi.sushi.Sushi;
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
import net.toshimichi.sushi.modules.combat.HoleMinerModule;
import net.toshimichi.sushi.utils.render.RenderUtils;

import java.awt.Color;

import static org.lwjgl.opengl.GL11.*;

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
        BlockPos breakingBlock = holeMineInfo.getSurroundPos();
        AxisAlignedBB box = getWorld().getBlockState(breakingBlock).getBoundingBox(getWorld(), breakingBlock)
                .offset(breakingBlock).grow(0.002);
        if (renderMode == RenderMode.FULL) glDisable(GL_DEPTH_TEST);
        RenderUtils.drawFilled(box, color.getCurrentColor());
        glEnable(GL_DEPTH_TEST);
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
