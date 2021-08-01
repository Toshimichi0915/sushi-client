package net.toshimichi.sushi.modules.render;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.toshimichi.sushi.config.Config;
import net.toshimichi.sushi.config.ConfigInjector;
import net.toshimichi.sushi.config.RootConfigurations;
import net.toshimichi.sushi.config.data.DoubleRange;
import net.toshimichi.sushi.config.data.IntRange;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.render.LivingLabelRenderEvent;
import net.toshimichi.sushi.events.render.OverlayRenderEvent;
import net.toshimichi.sushi.modules.*;
import net.toshimichi.sushi.utils.EntityState;
import net.toshimichi.sushi.utils.EntityUtils;
import net.toshimichi.sushi.utils.render.GuiUtils;
import net.toshimichi.sushi.utils.render.RenderUtils;
import net.toshimichi.sushi.utils.render.TextPreview;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.text.DecimalFormat;

public class NameTagsModule extends BaseModule {

    private static final DecimalFormat FORMAT = new DecimalFormat("0.0");

    @Config(id = "font", name = "Font")
    public String fontName = "Calibri";

//    @Config(id = "pts", name = "pts")
    public Integer pts = 40;

    @Config(id = "player", name = "Show players")
    public Boolean player = true;

    @Config(id = "self", name = "Show self", when = "player")
    public Boolean self = false;

    @Config(id = "mob", name = "Show mobs")
    public Boolean mob = false;

    @Config(id = "passive", name = "Show Passive Mobs", when = "mob")
    public Boolean passive = false;

    @Config(id = "neutral", name = "Show Neutral Mobs", when = "mob")
    public Boolean neutral = false;

    @Config(id = "hostile", name = "Show Hostile Mobs", when = "mob")
    public Boolean hostile = false;

    @Config(id = "scale_multiplier", name = "Scale Multiplier")
    public DoubleRange scaleMultiplier = new DoubleRange(3, 10, 1, 0.1, 1);

    @Config(id = "min_scale", name = "Min Scale")
    public IntRange minScale = new IntRange(1, 100, 1,  1);

    @Config(id = "max_scale", name = "Max Scale")
    public IntRange maxScale = new IntRange(100, 100, 1,  1);

    public NameTagsModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        new ConfigInjector(provider).inject(this);
        GuiUtils.prepareFont(fontName, pts);
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
    }

    private boolean canShow(EntityLivingBase entity) {
        if (entity instanceof EntityPlayer) {
            if (entity.getName().equals(getPlayer().getName())) return self;
            else return player;
        }
        if (!mob) return false;
        EntityState state = EntityUtils.getEntityType(entity);
        switch (state) {
            case PASSIVE:
                return passive;
            case NEUTRAL:
                return neutral;
            case HOSTILE:
                return hostile;
            default:
                throw new UnsupportedOperationException();
        }
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onOverlayRender(OverlayRenderEvent e) {
        for (Entity entity : getWorld().loadedEntityList) {
            if (!(entity instanceof EntityLivingBase)) continue;
            if (!canShow((EntityLivingBase) entity)) continue;

            Vec2f head = RenderUtils.fromWorld(RenderUtils.getInterpolatedPos(entity).add(0, entity.height, 0));
            if (head == null) continue;

            // set up matrix
            double scale = RenderUtils.getScale(RenderUtils.getInterpolatedPos(entity)) * scaleMultiplier.getCurrent();
            scale = MathHelper.clamp(scale, minScale.getCurrent() / 100D, maxScale.getCurrent() / 100D);
            GL11.glPushMatrix();
            GL11.glTranslated(head.x, head.y, 0);
            GL11.glScaled(scale, scale, 1);

            StringBuilder text = new StringBuilder(entity.getName());
            double health = ((EntityLivingBase) entity).getHealth() + ((EntityLivingBase) entity).getAbsorptionAmount();
            if (health > 12) text.append(" §a");
            else if (health > 6) text.append(" §e");
            else text.append(" §4");
            text.append(FORMAT.format(((EntityLivingBase) entity).getHealth()));
            TextPreview preview = GuiUtils.prepareText(text.toString(), fontName, Color.WHITE, pts, false);
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

    @EventHandler(timing = EventTiming.PRE)
    public void onLivingLabelRender(LivingLabelRenderEvent e) {
        if (!(e.getEntity() instanceof EntityLivingBase)) return;
        if (!canShow((EntityLivingBase) e.getEntity())) return;
        e.setCancelled(true);
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
