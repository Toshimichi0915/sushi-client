package net.sushiclient.client.modules.combat;

import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.packet.PacketSendEvent;
import net.sushiclient.client.modules.*;

public class CriticalsModule extends BaseModule {
    public CriticalsModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
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
    public void onPacketSend(PacketSendEvent e) {
        if (!(e.getPacket() instanceof CPacketUseEntity)) return;
        CPacketUseEntity packet = (CPacketUseEntity) e.getPacket();
        if (packet.getAction() != CPacketUseEntity.Action.ATTACK) return;
        sendPacket(new CPacketPlayer.Position(getPlayer().posX, getPlayer().posY + 0.1, getPlayer().posZ, false));
        sendPacket(new CPacketPlayer.Position(getPlayer().posX, getPlayer().posY, getPlayer().posZ, false));
    }

    @Override
    public String getDefaultName() {
        return "Criticals";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.COMBAT;
    }
}
