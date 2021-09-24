package net.sushiclient.client.modules.player;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.MovementInput;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.input.InputUpdateEvent;
import net.sushiclient.client.events.packet.PacketSendEvent;
import net.sushiclient.client.events.player.PlayerAttackEvent;
import net.sushiclient.client.events.player.PlayerTurnEvent;
import net.sushiclient.client.events.player.UserCheckEvent;
import net.sushiclient.client.events.tick.ClientTickEvent;
import net.sushiclient.client.modules.*;
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

    @EventHandler(timing = EventTiming.PRE)
    public void onClientTick(ClientTickEvent e) {
        if (getPlayer().isDead) setEnabled(false);
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
    public void onTurn(PlayerTurnEvent e) {
        freecamPlayer.turn(e.getYaw(), e.getPitch());
        e.setCancelled(true);
    }

    @EventHandler(timing = EventTiming.PRE, priority = 5000)
    public void onPacketSend(PacketSendEvent e) {
        if (!(e.getPacket() instanceof CPacketUseEntity)) return;
        CPacketUseEntity packet = (CPacketUseEntity) e.getPacket();
        if (getPlayer().equals(packet.getEntityFromWorld(getWorld()))) {
            e.setCancelled(true);
        }
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
