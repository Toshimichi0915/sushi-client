package net.toshimichi.sushi.modules.render;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
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
import net.toshimichi.sushi.utils.combat.CevBreakAttack;
import net.toshimichi.sushi.utils.combat.CevBreakUtils;
import net.toshimichi.sushi.utils.player.RenderUtils;

import java.awt.Color;
import java.util.HashSet;
import java.util.List;

public class CevBreakHelperModule extends BaseModule {

    private final HashSet<BlockPos> candidates = new HashSet<>();

    @Config(id = "color", name = "Color")
    private EspColor color = new EspColor(Color.RED, false, true);

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
        for (BlockPos candidate : candidates) {
            AxisAlignedBB box = getWorld().getBlockState(candidate).getBoundingBox(getWorld(), candidate);
            box = box.offset(candidate).grow(0.002, 0.002, 0.002);
            RenderUtils.drawFilled(box, color.getCurrentColor());
        }
    }

    @EventHandler(timing = EventTiming.POST)
    public void onClientTick(ClientTickEvent e) {
        List<CevBreakAttack> attacks = CevBreakUtils.find(getPlayer());
        candidates.clear();
        for (CevBreakAttack attack : attacks) {
            if (attack.getObsidianPos() != null) candidates.add(attack.getObsidianPos());
        }
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
