package net.sushiclient.client.modules.client;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.util.math.AxisAlignedBB;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.tick.ClientTickEvent;
import net.sushiclient.client.modules.BaseModule;
import net.sushiclient.client.modules.Categories;
import net.sushiclient.client.modules.Category;
import net.sushiclient.client.modules.ModuleFactory;
import net.sushiclient.client.modules.Modules;

public class AntiCrasherModule extends BaseModule {

    public AntiCrasherModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
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
    public void onClientTick(ClientTickEvent e) {
        for (Entity entity : getWorld().loadedEntityList) {
            // entity bounding box check
            AxisAlignedBB box = entity.getEntityBoundingBox();
            if (box.maxX - box.minX > 20 && box.maxZ - box.minZ > 20) {
                entity.setEntityBoundingBox(new AxisAlignedBB(entity.posX + 1, entity.posY + 1, entity.posZ + 1,
                        entity.posX - 1, entity.posY - 1, entity.posZ - 1));
            }

            // area effect cloud radius check
            if (entity instanceof EntityAreaEffectCloud && ((EntityAreaEffectCloud) entity).getRadius() > 20) {
                ((EntityAreaEffectCloud) entity).setRadius(1);
            }
        }
    }

    @Override
    public String getDefaultName() {
        return "AntiCrasher";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.CLIENT;
    }
}
