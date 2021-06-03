package net.toshimichi.sushi.modules.render;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.toshimichi.sushi.config.Config;
import net.toshimichi.sushi.config.ConfigInjector;
import net.toshimichi.sushi.config.RootConfigurations;
import net.toshimichi.sushi.config.data.BlockName;
import net.toshimichi.sushi.config.data.EspColor;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.world.WorldRenderEvent;
import net.toshimichi.sushi.modules.*;
import net.toshimichi.sushi.utils.player.RenderUtils;
import net.toshimichi.sushi.utils.render.SearchMap;

import java.awt.Color;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL32.GL_DEPTH_CLAMP;

public class SearchModule extends BaseModule {

    private final Frustum frustum = new Frustum();

    private SearchMap searchMap;

    @Config(id = "blocks", name = "Blocks")
    private BlockName[] blocks = new BlockName[0];

    @Config(id = "outline", name = "Outline")
    private Boolean outline = true;

    @Config(id = "outline_color", name = "Outline Color", when = "outline")
    private EspColor outlineColor = new EspColor(new Color(255, 0, 0), false, true);

    @Config(id = "fill", name = "Fill")
    private Boolean fill = true;

    @Config(id = "fill_color", name = "Fill Color", when = "fill")
    private EspColor fillColor = new EspColor(new Color(255, 0, 0), false, true);

    @Config(id = "tracers", name = "Tracers")
    private Boolean tracers = true;

    @Config(id = "tracers_color", name = "Tracers Color", when = "tracers")
    private EspColor tracerColor = new EspColor(new Color(255, 0, 0), false, true);

    public SearchModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        new ConfigInjector(provider).inject(this);
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
        searchMap = new SearchMap(Arrays.stream(blocks).map(BlockName::toBlock).collect(Collectors.toList()));
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
        searchMap.close();
    }

    @EventHandler(timing = EventTiming.POST)
    public void onWorldRender(WorldRenderEvent e) {
        Vec3d interpolated = RenderUtils.getInterpolatedPos();
        frustum.setPosition(interpolated.x, interpolated.y, interpolated.z);
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_DEPTH_CLAMP);
        for (BlockPos pos : searchMap.getResults()) {
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
        glEnable(GL_DEPTH_TEST);
        glDisable(GL_DEPTH_CLAMP);
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
