package net.toshimichi.sushi.modules.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
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
import net.toshimichi.sushi.events.tick.ClientTickEvent;
import net.toshimichi.sushi.events.world.WorldRenderEvent;
import net.toshimichi.sushi.modules.*;
import net.toshimichi.sushi.modules.combat.CivBreakModule;
import net.toshimichi.sushi.utils.combat.CivBreakAttack;
import net.toshimichi.sushi.utils.combat.CivBreakUtils;
import net.toshimichi.sushi.utils.render.RenderUtils;
import net.toshimichi.sushi.utils.world.BlockUtils;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

public class CivBreakHelperModule extends BaseModule {

    private CivBreakAttack attack;

    @Config(id = "civ_break_module", name = "Civ Break Module")
    public String civBreakModule = "civ_break";

    @Config(id = "color", name = "Color")
    public EspColor color = new EspColor(Color.RED, false, true);


    public CivBreakHelperModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
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
        if (attack == null) return;
        BlockPos candidate = attack.getObsidianPos();
        AxisAlignedBB box = getWorld().getBlockState(candidate).getBoundingBox(getWorld(), candidate);
        box = box.offset(candidate).grow(0.002);
        GlStateManager.disableDepth();
        RenderUtils.drawFilled(box, color.getCurrentColor());
        GlStateManager.enableDepth();
    }

    @EventHandler(timing = EventTiming.POST)
    public void onClientTick(ClientTickEvent e) {
        Module module = Sushi.getProfile().getModules().getModule(civBreakModule);
        if (!(module instanceof CivBreakModule)) return;
        CivBreakModule civBreak = (CivBreakModule) module;
        List<CivBreakAttack> attacks = CivBreakUtils.find(getPlayer(), civBreak.getEnemyDamage(), civBreak.getSelfDamage());
        this.attack = null;
        if (attacks.isEmpty()) return;
        Collections.sort(attacks);
        CivBreakAttack attack = attacks.get(0);
        if (attack.getObsidianPos() == null) return;
        if (getWorld().getBlockState(attack.getObsidianPos()).getBlock() != Blocks.OBSIDIAN &&
                BlockUtils.findBlockPlaceInfo(getWorld(), attack.getObsidianPos()) == null) return;
        this.attack = attack;
    }

    @Override
    public String getDefaultName() {
        return "CivBreakHelper";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.RENDER;
    }
}
