package net.sushiclient.client.modules.combat;

import net.minecraft.entity.MoverType;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.sushiclient.client.config.Config;
import net.sushiclient.client.config.ConfigInjector;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.packet.PacketReceiveEvent;
import net.sushiclient.client.events.player.PlayerMoveEvent;
import net.sushiclient.client.events.player.PlayerPushEvent;
import net.sushiclient.client.events.player.PlayerPushOutOfBlocksEvent;
import net.sushiclient.client.modules.*;

public class VelocityModule extends BaseModule {

    @Config(id = "no_push", name = "No Push")
    public Boolean noPush = true;

    @Config(id = "no_piston", name = "No Piston")
    public Boolean noPiston = true;

    @Config(id = "no_block_push", name = "No Block Push")
    public Boolean noBlockPush = true;

    public VelocityModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        new ConfigInjector(provider).inject(this);
    }

    @Override
    public String getDefaultName() {
        return "Velocity";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.COMBAT;
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
    public void onPacketReceive(PacketReceiveEvent e) {
        if (e.getPacket() instanceof SPacketEntityVelocity) {
            e.setCancelled(true);
        } else if (e.getPacket() instanceof SPacketExplosion) {
            SPacketExplosion packet = (SPacketExplosion) e.getPacket();
            e.setPacket(new SPacketExplosion(packet.getX(), packet.getY(), packet.getZ(), packet.getStrength(), packet.getAffectedBlockPositions(), null));
        }
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onPush(PlayerPushEvent e) {
        if (noPush) e.setCancelled(true);
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onMove(PlayerMoveEvent e) {
        if (noPiston && e.getType() == MoverType.PISTON) e.setCancelled(true);
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onPushOutOfBlock(PlayerPushOutOfBlocksEvent e) {
        if (noBlockPush) e.setCancelled(true);
    }

}
