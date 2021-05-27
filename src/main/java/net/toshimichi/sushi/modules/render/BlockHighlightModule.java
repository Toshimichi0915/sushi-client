package net.toshimichi.sushi.modules.render;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.RootConfigurations;
import net.toshimichi.sushi.config.data.DoubleRange;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.world.BlockHighlightEvent;
import net.toshimichi.sushi.modules.*;
import net.toshimichi.sushi.utils.RenderUtils;
import org.lwjgl.opengl.GL11;

import java.awt.Color;

public class BlockHighlightModule extends BaseModule {

    private final Configuration<RenderMode> renderMode;
    private final Configuration<Boolean> outline;
    private final Configuration<Color> outlineColor;
    private final Configuration<DoubleRange> outlineWidth;
    private final Configuration<Boolean> fill;
    private final Configuration<Color> fillColor;

    public BlockHighlightModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        renderMode = provider.get("render mode", "Render Mode", null, RenderMode.class, RenderMode.SURFACE);
        outline = provider.get("outline", "Outline", null, Boolean.class, true);
        outlineColor = provider.get("outline_color", "Outline Color", null, Color.class, new Color(255, 0, 0), outline::getValue, false, 0);
        outlineWidth = provider.get("outline_width", "Outline Width", null, DoubleRange.class, new DoubleRange(1.0, 10, 0.1, 0.1, 1), outline::getValue, false, 0);
        fill = provider.get("fill", "Fill", null, Boolean.class, true);
        fillColor = provider.get("fill_color", "Fill Color", null, Color.class, new Color(255, 0, 0), fill::getValue, false, 0);
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
    public void onBlockHighlight(BlockHighlightEvent event) {
        event.setCancelled(true);
        RayTraceResult result = event.getTarget();
        BlockPos pos = result.getBlockPos();
        IBlockState state = getWorld().getBlockState(pos);
        if (state.getMaterial() == Material.AIR) return;
        AxisAlignedBB box = state.getBoundingBox(getWorld(), pos).offset(pos).grow(0.0005, 0.0005, 0.0005);
        if (renderMode.getValue() != RenderMode.NONE) {
            if (renderMode.getValue() == RenderMode.FULL) GL11.glDisable(GL11.GL_DEPTH_TEST);
            if (outline.getValue()) {
                RenderUtils.drawOutline(box, outlineColor.getValue(), outlineWidth.getValue().getCurrent());
            }
            if (fill.getValue()) {
                RenderUtils.drawFilled(box, fillColor.getValue());
            }
            GL11.glEnable(GL11.GL_DEPTH_TEST);
        }
    }

    @Override
    public String getDefaultName() {
        return "BlockHighlight";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.RENDER;
    }
}
