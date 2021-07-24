package net.toshimichi.sushi.modules.render;

import net.minecraft.entity.player.EntityPlayer;
import net.toshimichi.sushi.config.RootConfigurations;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.tick.ClientTickEvent;
import net.toshimichi.sushi.modules.*;
import net.toshimichi.sushi.utils.EntityUtils;

public class PlayerEspModule extends BaseModule {

    public PlayerEspModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
        for (EntityPlayer player : EntityUtils.getNearbyPlayers(Double.MAX_VALUE)) {
            player.setGlowing(false);
        }
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onClientTick(ClientTickEvent e) {
        for (EntityPlayer player : EntityUtils.getNearbyPlayers(Double.MAX_VALUE)) {
            player.setGlowing(true);
        }
    }

    @Override
    public String getDefaultName() {
        return "PlayerESP";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.RENDER;
    }
}
