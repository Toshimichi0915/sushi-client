package net.sushiclient.client.modules.render;

import net.minecraft.util.math.BlockPos;
import net.sushiclient.client.config.Config;
import net.sushiclient.client.config.ConfigInjector;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.config.data.EspColor;
import net.sushiclient.client.config.data.IntRange;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.player.PlayerUpdateEvent;
import net.sushiclient.client.events.world.WorldRenderEvent;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.utils.render.hole.HoleInfo;
import net.sushiclient.client.utils.render.hole.HoleRenderMode;
import net.sushiclient.client.utils.render.hole.HoleUtils;
import net.sushiclient.client.utils.world.BlockUtils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class HoleEspModule extends BaseModule implements ModuleSuffix {

    // use dirty way for performance
    @SuppressWarnings("unchecked")
    private final List<HoleInfo>[] partialHoles = new List[8];

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

    private int counter;

    public HoleEspModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        new ConfigInjector(provider).inject(this);
        for (int i = 0; i < partialHoles.length; i++) {
            partialHoles[i] = new ArrayList<>();
        }
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
        int minX, minY, minZ, maxX, maxY, maxZ;
        int index = counter++ % 8;
        List<HoleInfo> target = partialHoles[index];
        minX = counter / 4 % 2 - 1;
        minY = counter / 2 % 2 - 1;
        minZ = counter % 2 - 1;
        maxX = minX + 1;
        maxY = minY + 1;
        maxZ = minZ + 1;

        HashSet<HoleInfo> distinctHoles = new HashSet<>(target);

        // search for holes
        target.clear();
        BlockPos pos = BlockUtils.toBlockPos(getPlayer().getPositionVector());
        BlockPos from = new BlockPos(pos.getX() + horizontal.getCurrent() * minX, pos.getY() + vertical.getCurrent() * minY, pos.getZ() + horizontal.getCurrent() * minZ);
        BlockPos to = new BlockPos(pos.getX() + horizontal.getCurrent() * maxX, pos.getY() + vertical.getCurrent() * maxY, pos.getZ() + horizontal.getCurrent() * maxZ);
        HoleUtils.findHoles(getWorld(), from, to, doubleHole, target::add);

        for (List<HoleInfo> hole : partialHoles) {
            synchronized (hole) {
                distinctHoles.addAll(hole);
            }
        }
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

    public List<HoleInfo> getHoles() {
        return new ArrayList<>(holes);
    }

    @Override
    public String getDefaultName() {
        return "HoleESP";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.RENDER;
    }

    @Override
    public String getSuffix() {
        return mode.getName();
    }
}
