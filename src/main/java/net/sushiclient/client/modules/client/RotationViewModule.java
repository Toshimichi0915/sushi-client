package net.sushiclient.client.modules.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.CPacketPlayer;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.packet.PacketSendEvent;
import net.sushiclient.client.events.render.EntityRenderEvent;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.utils.player.PositionUtils;

public class RotationViewModule extends BaseModule {

    private volatile float packetYaw;
    private volatile float packetPitch;
    private float yaw;
    private float pitch;
    private float lastYaw;
    private float lastPitch;

    public RotationViewModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
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

    @EventHandler(timing = EventTiming.POST)
    public void onPacketSend(PacketSendEvent e) {
        if (!(e.getPacket() instanceof CPacketPlayer)) return;
        CPacketPlayer packet = (CPacketPlayer) e.getPacket();
        this.packetYaw = packet.getYaw(getPlayer().rotationYaw);
        this.packetPitch = packet.getPitch(getPlayer().rotationPitch);
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onPreRender(EntityRenderEvent e) {
        if (!PositionUtils.getDesyncMode().isRotationDesync()) return;
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (e.getEntityIn() != player) return;
        yaw = player.rotationYaw;
        pitch = player.rotationPitch;
        lastYaw = player.prevRotationYaw;
        lastPitch = player.prevRotationPitch;
        player.rotationYaw = packetYaw;
        player.rotationPitch = packetPitch;
        player.prevRotationYaw = packetYaw;
        player.prevRotationPitch = packetPitch;
    }

    @EventHandler(timing = EventTiming.POST)
    public void onPostRender(EntityRenderEvent e) {
        if (!PositionUtils.getDesyncMode().isRotationDesync()) return;
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (e.getEntityIn() != player) return;
        player.rotationYaw = yaw;
        player.rotationPitch = pitch;
        player.prevRotationYaw = lastYaw;
        player.prevRotationPitch = lastPitch;
    }

    @Override
    public String getDefaultName() {
        return "RotationView";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.CLIENT;
    }
}
