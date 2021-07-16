package net.toshimichi.sushi.modules.render;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.RootConfigurations;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.world.WorldRenderEvent;
import net.toshimichi.sushi.modules.*;
import net.toshimichi.sushi.utils.render.RenderUtils;

import java.awt.Color;

import static org.lwjgl.opengl.GL11.*;

public class TracersModule extends BaseModule {

    private static final float[] RED_HSB = Color.RGBtoHSB(255, 0, 0, null);
    private static final float[] GREEN_HSB = Color.RGBtoHSB(0, 255, 0, null);
    private final Configuration<Boolean> relative;

    public TracersModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        relative = provider.get("relative", "Relative", null, Boolean.class, true);
    }

    private Color getColor(double distance) {
        double gradient = MathHelper.clamp(0, distance / 54, 1);
        double hue = MathHelper.clampedLerp(RED_HSB[0], GREEN_HSB[0], gradient);
        double saturation = MathHelper.clampedLerp(RED_HSB[1], GREEN_HSB[1], gradient);
        double brightness = MathHelper.clampedLerp(RED_HSB[2], GREEN_HSB[2], gradient);
        return Color.getHSBColor((float) hue, (float) saturation, (float) brightness);
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
        for (Entity entity : getWorld().loadedEntityList) {
            if (!(entity instanceof EntityPlayer)) continue;
            if (entity.getName().equals(getPlayer().getName())) continue;
            glDisable(GL_DEPTH_TEST);
            Color color = getColor(MathHelper.sqrt(entity.getDistanceSq(getPlayer())));
            Vec3d cameraCenter = relative.getValue() ?
                    RenderUtils.getViewerPos().add(RenderUtils.getCameraPos()).subtract(RenderUtils.getInterpolatedPos()) : RenderUtils.getCameraPos();
            RenderUtils.drawLine(cameraCenter, RenderUtils.getInterpolatedPos(entity), color, 1);
            glEnable(GL_DEPTH_TEST);
        }
    }

    @Override
    public String getDefaultName() {
        return "Tracers";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.RENDER;
    }
}
