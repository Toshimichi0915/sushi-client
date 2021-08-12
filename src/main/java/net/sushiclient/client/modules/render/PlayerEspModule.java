package net.sushiclient.client.modules.render;

import net.minecraft.entity.player.EntityPlayer;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.tick.ClientTickEvent;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.utils.EntityUtils;

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
