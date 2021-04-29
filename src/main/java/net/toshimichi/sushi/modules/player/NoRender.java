package net.toshimichi.sushi.modules.player;

import net.minecraft.world.EnumSkyBlock;
import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.Configurations;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.client.UpdateLightEvent;
import net.toshimichi.sushi.modules.*;

public class NoRender extends BaseModule {

    private final Configuration<Boolean> skyLight;
    private final Configuration<Boolean> blockLight;

    public NoRender(String id, Modules modules, Categories categories, Configurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        skyLight = provider.get("sky_light", "Sky Light", null, Boolean.class, false);
        blockLight = provider.get("block_light", "Block Light", null, Boolean.class, false);
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
    }

    @Override
    public String getDefaultName() {
        return "NoRender";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.PLAYER;
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onCheckLight(UpdateLightEvent e) {
        if ((skyLight.getValue() && e.getEnumSkyBlock() == EnumSkyBlock.SKY) ||
                (blockLight.getValue() && e.getEnumSkyBlock() == EnumSkyBlock.BLOCK))
            e.setCancelled(true);
    }
}