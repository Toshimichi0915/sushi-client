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
import net.toshimichi.sushi.utils.world.BlockUtils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class HoleEspModule extends BaseModule {

    private final ArrayList<HoleInfo> holes1 = new ArrayList<>();
    private final ArrayList<HoleInfo> holes2 = new ArrayList<>();
    private final ArrayList<HoleInfo> holes3 = new ArrayList<>();
    private final ArrayList<HoleInfo> holes4 = new ArrayList<>();
    private volatile List<HoleInfo> holes = new ArrayList<>();

    @Config(id = "mode", name = "Mode")
    public HoleRenderMode mode = HoleRenderMode.FILL;

    @Config(id = "double", name = "Double")
    public Boolean doubleHole = false;

    @Config(id = "vertical", name = "Vertical")
    public IntRange vertical = new IntRange(5, 10, 1, 1);

    @Config(id = "horizontal", name = "Horizontal")
    public IntRange horizontal = new IntRange(5, 20, 1, 1);

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
        int minX, minZ, maxX, maxZ;
        ArrayList<HoleInfo> target;
        switch (TickUtils.current() % 4) {
            case 0:
                minX = minZ = -1;
                maxX = maxZ = 0;
                target = holes1;
                break;
            case 1:
                minX = -1;
                maxX = minZ = 0;
                maxZ = 1;
                target = holes2;
                break;
            case 2:
                minZ = -1;
                minX = maxZ = 0;
                maxX = 1;
                target = holes3;
                break;
            default: // case 3:
                minX = minZ = 0;
                maxX = maxZ = 1;
                target = holes4;
        }
        HashSet<HoleInfo> distinctHoles = new HashSet<>(target);

        // search for holes
        target.clear();
        BlockPos pos = BlockUtils.toBlockPos(getPlayer().getPositionVector());
        BlockPos from = new BlockPos(pos.getX() + horizontal.getCurrent() * minX, pos.getY() - vertical.getCurrent(), pos.getZ() + horizontal.getCurrent() * minZ);
        BlockPos to = new BlockPos(pos.getX() + horizontal.getCurrent() * maxX, pos.getY() + vertical.getCurrent(), pos.getZ() + horizontal.getCurrent() * maxZ);
        HoleUtils.findHoles(getWorld(), from, to, doubleHole, target::add);

        distinctHoles.addAll(holes1);
        distinctHoles.addAll(holes2);
        distinctHoles.addAll(holes3);
        distinctHoles.addAll(holes4);
        ArrayList<HoleInfo> holes = new ArrayList<>(distinctHoles);
        Collections.sort(holes);
        this.holes = holes;
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
