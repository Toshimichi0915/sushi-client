package net.sushiclient.client.modules.render;

import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.sushiclient.client.config.Config;
import net.sushiclient.client.config.ConfigInjector;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.config.data.DoubleRange;
import net.sushiclient.client.config.data.IntRange;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.render.LivingLabelRenderEvent;
import net.sushiclient.client.events.render.OverlayRenderEvent;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.utils.EntityType;
import net.sushiclient.client.utils.render.GuiUtils;
import net.sushiclient.client.utils.render.RenderUtils;
import net.sushiclient.client.utils.render.TextPreview;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

public class NameTagsModule extends BaseModule {

    private static final DecimalFormat FORMAT = new DecimalFormat("0.0");

    @Config(id = "font", name = "Font")
    public String fontName = "Calibri";

    //    @Config(id = "pts", name = "pts")
    public Integer pts = 40;

    @Config(id = "player", name = "Show players")
    public Boolean player = true;

    @Config(id = "show_ping", name = "Show Ping", when = "player")
    public Boolean showPing = true;

    @Config(id = "show_hp", name = "Show HP", when = "player")
    public Boolean showHealth = true;

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

    @Config(id = "scale_multiplier", name = "Scale")
    public DoubleRange scaleMultiplier = new DoubleRange(3, 10, 1, 0.1, 1);

    @Config(id = "min_scale", name = "Min Scale")
    public IntRange minScale = new IntRange(1, 100, 1, 1);

    @Config(id = "max_scale", name = "Max Scale")
    public IntRange maxScale = new IntRange(100, 100, 1, 1);

    private static final double SCALE = 3;
    private static final int MARGIN = 7;

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

    @EventHandler(timing = EventTiming.PRE)
    public void onOverlayRender(OverlayRenderEvent e) {
        ArrayList<Entity> entityList = new ArrayList<>(getWorld().loadedEntityList);
        entityList.sort(Comparator.comparingDouble(it -> it.getDistanceSq(getPlayer())));
        for (Entity entity : entityList) {
            if (!(entity instanceof EntityLivingBase)) continue;
            EntityLivingBase entityLiving = (EntityLivingBase) entity;
            if (!EntityType.match(entityLiving, player, self, mob, passive, neutral, hostile)) continue;

            Vec2f head = RenderUtils.fromWorld(RenderUtils.getInterpolatedPos(entity).add(0, entity.height, 0));
            if (head == null) continue;

            GlStateManager.enableDepth();
            GlStateManager.enableBlend();

            // set up matrix
            double scale = RenderUtils.getScale(RenderUtils.getInterpolatedPos(entity)) * scaleMultiplier.getCurrent();
            scale = MathHelper.clamp(scale, minScale.getCurrent() / 100D, maxScale.getCurrent() / 100D);
            GlStateManager.pushMatrix();
            GlStateManager.translate(head.x, head.y, 0);
            GlStateManager.scale(scale, scale, 1);

            StringBuilder text = new StringBuilder(entity.getName());
            NetworkPlayerInfo info = getConnection().getPlayerInfo(entity.getUniqueID());
            if (showPing && entity instanceof EntityPlayer && info != null) {
                text.append(' ');
                text.append(info.getResponseTime());
                text.append("ms");
            }
            if (showHealth) {
                double health = entityLiving.getHealth() + entityLiving.getAbsorptionAmount();
                if (health > 12) text.append(" §a");
                else if (health > 6) text.append(" §e");
                else text.append(" §4");
                text.append(FORMAT.format(health));
            }
            TextPreview preview = GuiUtils.prepareText(text.toString(), fontName, Color.WHITE, pts, false);
            double width = preview.getWidth();
            double height = preview.getHeight();
            double textX = -width / 2;
            double textY = -height - 5;
            GuiUtils.drawRect(textX - 5, textY - 1, width + 10, height + 2, new Color(0, 0, 0, 100));
            GuiUtils.drawOutline(textX - 5, textY - 1, width + 10, height + 2, new Color(0, 0, 0), 1);
            preview.draw(textX, textY);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();

            GlStateManager.translate(head.x, head.y, 0);
            GlStateManager.scale(scale * SCALE, scale * SCALE, 0);
            RenderItem renderer = getClient().getRenderItem();
            double startX = -MARGIN * SCALE * 3;
            ArrayList<ItemStack> inventory = new ArrayList<>();
            inventory.add(entityLiving.getHeldItemMainhand());
            if (entityLiving instanceof EntityPlayer) {
                EntityPlayer other = (EntityPlayer) entityLiving;
                inventory.add(other.inventory.armorInventory.get(3));
                inventory.add(other.inventory.armorInventory.get(2));
                inventory.add(other.inventory.armorInventory.get(1));
                inventory.add(other.inventory.armorInventory.get(0));
                inventory.add(other.inventory.offHandInventory.get(0));
            }

            int index = 0;
            for (ItemStack item : inventory) {
                int itemX = (int) (MARGIN * SCALE * index + startX);
                int itemY = (int) (-SCALE * 12);
                RenderHelper.enableGUIStandardItemLighting();
                renderer.renderItemAndEffectIntoGUI(item, itemX, itemY);
                renderer.renderItemOverlays(getClient().fontRenderer, item, itemX, itemY);
                RenderHelper.disableStandardItemLighting();
                GlStateManager.pushMatrix();
                GlStateManager.translate(itemX, itemY, 0);
                GlStateManager.scale(0.3, 0.3, 0);
                int index2 = 0;
                for (Map.Entry<Enchantment, Integer> entry : EnchantmentHelper.getEnchantments(item).entrySet()) {
                    // Enchantment can be null when illegal enchant is used
                    if (entry.getKey() == null || entry.getValue() == null) continue;
                    String translated = entry.getKey().getTranslatedName(entry.getValue());
                    String enchName = translated.substring(0, 2) + " " + entry.getValue();
                    getClient().fontRenderer.drawString(enchName, 0, index2 * 9, Color.WHITE.getRGB());
                    index2++;
                }
                GlStateManager.popMatrix();
                index++;
            }

            GlStateManager.popMatrix();
        }
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onLivingLabelRender(LivingLabelRenderEvent e) {
        if (!EntityType.match(e.getEntity(), player, self, mob, passive, neutral, hostile)) return;
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
