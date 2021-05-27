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

    private final Configuration<Color> outlineColor;
    private final Configuration<DoubleRange> outlineWidth;
    private final Configuration<OutlineMode> outlineMode;

    public BlockHighlightModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        outlineColor = provider.get("outline_color", "Outline Color", null, Color.class, new Color(255, 0, 0));
        outlineWidth = provider.get("outline_width", "Outline Width", null, DoubleRange.class, new DoubleRange(1.0, 10, 0.1, 0.1, 1));
        outlineMode = provider.get("outline_mode", "Outline Mode", null, OutlineMode.class, OutlineMode.SURFACE);
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
        AxisAlignedBB box = state.getBoundingBox(getWorld(), pos).offset(pos).grow(0.0001, 0.0001, 0.0001);
        if (outlineMode.getValue() != OutlineMode.NONE) {
            if (outlineMode.getValue() == OutlineMode.FULL) GL11.glDisable(GL11.GL_DEPTH_TEST);
            RenderUtils.drawOutline(box, outlineColor.getValue(), outlineWidth.getValue().getCurrent());
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
