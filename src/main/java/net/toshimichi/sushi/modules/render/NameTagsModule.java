package net.toshimichi.sushi.modules.render;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec2f;
import net.toshimichi.sushi.config.Config;
import net.toshimichi.sushi.config.ConfigInjector;
import net.toshimichi.sushi.config.RootConfigurations;
import net.toshimichi.sushi.config.data.DoubleRange;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.tick.OverlayRenderEvent;
import net.toshimichi.sushi.modules.*;
import net.toshimichi.sushi.utils.render.GuiUtils;
import net.toshimichi.sushi.utils.render.RenderUtils;
import net.toshimichi.sushi.utils.render.TextPreview;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.text.DecimalFormat;

public class NameTagsModule extends BaseModule {

    private static final DecimalFormat FORMAT = new DecimalFormat("0.0");

    @Config(id = "scale_multiplier", name = "Scale Multiplier")
    private final DoubleRange scaleMultiplier = new DoubleRange(3, 10, 1, 0.1, 1);

    public NameTagsModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
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
    public void onOverlayRender(OverlayRenderEvent e) {
        for (Entity entity : getWorld().loadedEntityList) {
            if (!(entity instanceof EntityLivingBase)) continue;
            Vec2f head = RenderUtils.fromWorld(RenderUtils.getInterpolatedPos(entity).add(0, entity.height, 0));
            if (head == null) continue;

            // set up matrix
            double scale = RenderUtils.getScale(RenderUtils.getInterpolatedPos(entity)) * scaleMultiplier.getCurrent();
            GL11.glPushMatrix();
            GL11.glTranslated(head.x, head.y, 0);
            GL11.glScaled(scale, scale, 1);

            StringBuilder text = new StringBuilder(entity.getName());
            text.append(' ');
            text.append(FORMAT.format(((EntityLiving) entity).getHealth()));
            TextPreview preview = GuiUtils.prepareText(text.toString(), "Calibri", Color.WHITE, 30, true);
            double width = preview.getWidth();
            double height = preview.getHeight();
            double x = -width / 2;
            double y = -height - 10;
            GuiUtils.drawRect(x - 5, y - 1, width + 10, height + 2, new Color(0, 0, 0, 100));
            GuiUtils.drawOutline(x - 5, y - 1, width + 10, height + 2, new Color(0, 0, 0), 1);
            preview.draw(x, y);

            GL11.glPopMatrix();
        }
    }

    @Override
    public String getDefaultName() {
        return "NameTags";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.RENDER;
    }
}
