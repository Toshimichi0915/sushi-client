package net.toshimichi.sushi.modules.player;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MovementInput;
import net.toshimichi.sushi.config.RootConfigurations;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.input.InputUpdateEvent;
import net.toshimichi.sushi.events.player.PlayerAttackEvent;
import net.toshimichi.sushi.events.player.UserCheckEvent;
import net.toshimichi.sushi.modules.*;
import org.apache.commons.lang3.RandomUtils;

public class FreecamModule extends BaseModule {

    private FreecamPlayer freecamPlayer;

    public FreecamModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
        freecamPlayer = new FreecamPlayer(getWorld());
        int entityId = RandomUtils.nextInt(0, Integer.MAX_VALUE) + Integer.MIN_VALUE;
        getWorld().addEntityToWorld(entityId, freecamPlayer);
        Minecraft.getMinecraft().setRenderViewEntity(freecamPlayer);
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
        getWorld().removeEntityFromWorld(freecamPlayer.getEntityId());
        Minecraft.getMinecraft().setRenderViewEntity(Minecraft.getMinecraft().player);
        freecamPlayer = null;
    }

    @EventHandler(timing = EventTiming.POST)
    public void onInputUpdate(InputUpdateEvent e) {
        MovementInput input = Minecraft.getMinecraft().player.movementInput;
        input.forwardKeyDown = false;
        input.backKeyDown = false;
        input.leftKeyDown = false;
        input.rightKeyDown = false;
        input.jump = false;
        input.sneak = false;
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onPlayerAttack(PlayerAttackEvent e) {
        if (e.getTarget() == getPlayer()) e.setCancelled(true);
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onUserCheck(UserCheckEvent e) {
        e.setUser(false);
    }

    @Override
    protected boolean isTemporaryByDefault() {
        return true;
    }

    @Override
    public String getDefaultName() {
        return "Freecam";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.PLAYER;
    }
}
