package net.sushiclient.client.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.CPacketPlayer;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.packet.PacketSendEvent;
import net.sushiclient.client.events.player.PlayerPacketEvent;
import net.sushiclient.client.utils.player.PlayerUtils;
import net.sushiclient.client.utils.player.PositionMask;
import net.sushiclient.client.utils.player.PositionUtils;

public class DesyncHandler {

    @EventHandler(timing = EventTiming.PRE, priority = 8000)
    public void onPrePositionPacket(PacketSendEvent e) {
        if (!(e.getPacket() instanceof CPacketPlayer)) return;

        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null) return;

        CPacketPlayer cp = (CPacketPlayer) e.getPacket();
        PositionUtils.update();
        PositionMask mode = PositionUtils.getDesyncMode();
        boolean position = mode.isPositionDesync();
        boolean rotation = mode.isRotationDesync();
        boolean flying = mode.isOnGroundDesync();
        double x = PositionUtils.getX();
        double y = PositionUtils.getY();
        double z = PositionUtils.getZ();
        float yaw = PositionUtils.getYaw();
        float pitch = PositionUtils.getPitch();
        boolean onGround = PositionUtils.isOnGround();
        e.setPacket(PlayerUtils.newCPacketPlayer(cp, x, y, z, yaw, pitch, onGround, position, rotation, flying));
    }

    @EventHandler(timing = EventTiming.POST)
    public void onPostPlayerPacket(PacketSendEvent e) {
        if (!(e.getPacket() instanceof CPacketPlayer)) return;
        PositionUtils.invokeAll();
    }

    @EventHandler(timing = EventTiming.PRE, priority = 10000)
    public void onPrePlayerPacket(PlayerPacketEvent e) {
        PositionUtils.update();
        if (!PositionUtils.getDesyncMode().equals(PositionMask.NONE)) {
            PositionUtils.updatePositionUpdateTicks();
        }
    }
}
