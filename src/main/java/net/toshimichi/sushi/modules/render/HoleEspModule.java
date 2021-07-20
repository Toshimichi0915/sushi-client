package net.toshimichi.sushi.modules.render;

import net.minecraft.util.math.BlockPos;
import net.toshimichi.sushi.config.Config;
import net.toshimichi.sushi.config.ConfigInjector;
import net.toshimichi.sushi.config.RootConfigurations;
import net.toshimichi.sushi.config.data.EspColor;
import net.toshimichi.sushi.config.data.IntRange;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.player.PlayerUpdateEvent;
import net.toshimichi.sushi.events.world.WorldRenderEvent;
import net.toshimichi.sushi.modules.*;
import net.toshimichi.sushi.utils.TickUtils;
import net.toshimichi.sushi.utils.render.hole.HoleInfo;
import net.toshimichi.sushi.utils.render.hole.HoleRenderMode;
import net.toshimichi.sushi.utils.render.hole.HoleUtils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;

public class HoleEspModule extends BaseModule {

    private volatile ArrayList<HoleInfo> holes = new ArrayList<>();

    @Config(id = "mode", name = "Mode")
    public HoleRenderMode mode = HoleRenderMode.FILL;

    @Config(id = "vertical", name = "Vertical")
    public IntRange vertical = new IntRange(10, 20, 5, 1);

    @Config(id = "horizontal", name = "Horizontal")
    public IntRange horizontal = new IntRange(10, 20, 5, 1);

    @Config(id = "obsidian_color", name = "Obsidian Color")
    public EspColor obsidianColor = new EspColor(new Color(255, 0, 0, 100), false, true);

    @Config(id = "bedrock_color", name = "Bedrock Color")
    public EspColor bedrockColor = new EspColor(new Color(0, 255, 0, 100), false, true);

    public HoleEspModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
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

    @EventHandler(timing = EventTiming.PRE)
    public void onPlayerUpdate(PlayerUpdateEvent e) {
        if (TickUtils.current() % 5 != 0) return;
        BlockPos pos = getPlayer().getPosition();
        ArrayList<HoleInfo> copy = new ArrayList<>();
        BlockPos from = new BlockPos(pos.getX() - horizontal.getCurrent(), pos.getY() - vertical.getCurrent(), pos.getZ() - horizontal.getCurrent());
        BlockPos to = new BlockPos(pos.getX() + horizontal.getCurrent(), pos.getY() + vertical.getCurrent(), pos.getZ() + horizontal.getCurrent());
        HoleUtils.findHoles(getWorld(), from, to, copy::add);
        Collections.sort(copy);
        holes = copy;
    }

    @EventHandler(timing = EventTiming.POST)
    public void onWorldRender(WorldRenderEvent e) {
        for (HoleInfo info : holes) {
            mode.render(getWorld(), info, obsidianColor, bedrockColor);
        }
    }

    @Override
    public String getDefaultName() {
        return "HoleESP";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.RENDER;
    }
}
