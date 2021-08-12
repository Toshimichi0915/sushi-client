package net.sushiclient.client.modules.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.sushiclient.client.config.Config;
import net.sushiclient.client.config.ConfigInjector;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.config.data.BlockName;
import net.sushiclient.client.config.data.EspColor;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.world.WorldRenderEvent;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.utils.render.RenderUtils;
import net.sushiclient.client.utils.render.SearchMap;

import java.awt.Color;
import java.util.Arrays;
import java.util.stream.Collectors;

public class SearchModule extends BaseModule {

    private final Frustum frustum = new Frustum();

    private SearchMap searchMap;

    @Config(id = "blocks", name = "Blocks")
    public BlockName[] blocks = new BlockName[0];

    @Config(id = "outline", name = "Outline")
    public Boolean outline = true;

    @Config(id = "outline_color", name = "Outline Color", when = "outline")
    public EspColor outlineColor = new EspColor(new Color(255, 0, 0), false, true);

    @Config(id = "fill", name = "Fill")
    public Boolean fill = true;

    @Config(id = "fill_color", name = "Fill Color", when = "fill")
    public EspColor fillColor = new EspColor(new Color(255, 0, 0), false, true);

    @Config(id = "tracers", name = "Tracers")
    public Boolean tracers = true;

    @Config(id = "tracers_color", name = "Tracers Color", when = "tracers")
    public EspColor tracerColor = new EspColor(new Color(255, 0, 0), false, true);

    public SearchModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        new ConfigInjector(provider).inject(this);
    }

    @Override
    public void onEnable() {
        searchMap = new SearchMap(Arrays.stream(blocks).map(BlockName::toBlock).collect(Collectors.toList()));
        EventHandlers.register(this);
    }

    @Override
    public void onDisable() {
        searchMap.close();
        EventHandlers.unregister(this);
    }

    @EventHandler(timing = EventTiming.POST)
    public void onWorldRender(WorldRenderEvent e) {
        Vec3d interpolated = RenderUtils.getInterpolatedPos();
        frustum.setPosition(interpolated.x, interpolated.y, interpolated.z);
        GlStateManager.disableDepth();
        for (BlockPos pos : searchMap.getResult()) {
            AxisAlignedBB box = getWorld().getBlockState(pos).getBoundingBox(getWorld(), pos).offset(pos);
            if (tracers) {
                RenderUtils.drawLine(RenderUtils.getCameraPos(), box.getCenter(), tracerColor.getCurrentColor(), 1);
            }
            if (!frustum.isBoundingBoxInFrustum(box)) continue;
            if (outline) {
                RenderUtils.drawOutline(box, outlineColor.getCurrentColor(), 1);
            }
            if (fill) {
                RenderUtils.drawFilled(box, fillColor.getCurrentColor());
            }
        }
        GlStateManager.enableDepth();
    }

    @Override
    public String getDefaultName() {
        return "Search";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.RENDER;
    }
}
