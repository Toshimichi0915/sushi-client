package net.sushiclient.client.modules.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.sushiclient.client.Sushi;
import net.sushiclient.client.config.Config;
import net.sushiclient.client.config.ConfigInjector;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.config.data.EspColor;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.tick.ClientTickEvent;
import net.sushiclient.client.events.world.WorldRenderEvent;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.modules.combat.CivBreakModule;
import net.sushiclient.client.utils.combat.CivBreakAttack;
import net.sushiclient.client.utils.combat.CivBreakUtils;
import net.sushiclient.client.utils.render.RenderUtils;
import net.sushiclient.client.utils.world.BlockUtils;

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
        List<CivBreakAttack> attacks = CivBreakUtils.find(getPlayer(), civBreak.getEnemyDamage(), civBreak.getSelfDamage(), civBreak.getDamageRatio());
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
