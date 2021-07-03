package net.toshimichi.sushi.modules.render;

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
import net.toshimichi.sushi.modules.combat.CevBreakModule;
import net.toshimichi.sushi.utils.combat.CevBreakAttack;
import net.toshimichi.sushi.utils.combat.CevBreakUtils;
import net.toshimichi.sushi.utils.render.RenderUtils;
import net.toshimichi.sushi.utils.world.BlockUtils;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class CevBreakHelperModule extends BaseModule {

    private CevBreakAttack attack;

    @Config(id = "cev_break_id", name = "CevBreak ID")
    public String cevBreak = "cev_break";

    @Config(id = "color", name = "Color")
    public EspColor color = new EspColor(Color.RED, true);

    public CevBreakHelperModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
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
        glDisable(GL_DEPTH_TEST);
        RenderUtils.drawFilled(box, color.getCurrentColor());
        glEnable(GL_DEPTH_TEST);
    }

    @EventHandler(timing = EventTiming.POST)
    public void onClientTick(ClientTickEvent e) {
        Module cevBreak = Sushi.getProfile().getModules().getModule(this.cevBreak);
        if (!(cevBreak instanceof CevBreakModule)) return;

        List<CevBreakAttack> attacks = CevBreakUtils.find(getPlayer(), ((CevBreakModule) cevBreak).getBreakingBlock());
        this.attack = null;
        if (attacks.isEmpty()) return;
        Collections.sort(attacks);
        CevBreakAttack attack = attacks.get(0);
        if (attack.getObsidianPos() == null) return;
        if (getWorld().getBlockState(attack.getObsidianPos()).getBlock() != Blocks.OBSIDIAN &&
                BlockUtils.findBlockPlaceInfo(getWorld(), attack.getObsidianPos()) == null) return;
        this.attack = attack;
    }

    @Override
    public String getDefaultName() {
        return "CevBreakHelper";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.RENDER;
    }
}
