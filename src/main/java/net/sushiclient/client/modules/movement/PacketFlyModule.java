package net.sushiclient.client.modules.movement;

import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.config.data.DoubleRange;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.packet.PacketReceiveEvent;
import net.sushiclient.client.events.player.PlayerPacketEvent;
import net.sushiclient.client.events.player.PlayerTravelEvent;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.utils.EntityUtils;
import net.sushiclient.client.utils.player.MovementUtils;

public class PacketFlyModule extends BaseModule {

    private final Configuration<DoubleRange> horizontalSpeed;
    private final Configuration<DoubleRange> verticalSpeed;
    private final Configuration<DoubleRange> horizontalPacket;
    private final Configuration<DoubleRange> verticalPacket;
    private int teleportId;
    private int teleportId2;

    public PacketFlyModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        horizontalSpeed = provider.get("horizontal_speed", "Horizontal Speed", null, DoubleRange.class, new DoubleRange(8, 20, 0.1, 0.1, 2));
        verticalSpeed = provider.get("vertical_speed", "Vertical Speed", null, DoubleRange.class, new DoubleRange(8, 20, 0.1, 0.1, 2));
        horizontalPacket = provider.get("horizontal_packet", "Horizontal Packet", null, DoubleRange.class, new DoubleRange(100, 200, 0.1, 2, 2));
        verticalPacket = provider.get("vertical_packet", "Vertical Packet", null, DoubleRange.class, new DoubleRange(100, 200, 0.1, 2, 2));
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
        teleportId = 0;
        teleportId2 = 0;
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onPlayerTravel(PlayerTravelEvent e) {
        getPlayer().noClip = EntityUtils.isInsideBlock(getPlayer());
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onPlayerUpdate(PlayerPacketEvent e) {
        e.setCancelled(true);
        if (getPlayer().ticksExisted < 5) return;

        if (teleportId2 <= teleportId) {
            teleportId2 = teleportId + 1;
        }
        if (teleportId2 != 0) {
            getConnection().sendPacket(new CPacketPlayer.Position(getPlayer().posX, getPlayer().posY - 1337, getPlayer().posZ, true));
            getConnection().sendPacket(new CPacketConfirmTeleport(teleportId2));
        }
        teleportId2++;
        getConnection().sendPacket(new CPacketPlayer.Position(getPlayer().posX, getPlayer().posY, getPlayer().posZ, true));

        Vec3d movement = MovementUtils.getMoveInputs(getPlayer()).normalize();
        Vec2f delta = MovementUtils.toWorld(new Vec2f((float) movement.x, (float) movement.z), getPlayer().rotationYaw);
        getPlayer().motionX = delta.x * horizontalSpeed.getValue().getCurrent();
        getPlayer().motionY = movement.y * verticalSpeed.getValue().getCurrent();
        getPlayer().motionZ = delta.y * horizontalSpeed.getValue().getCurrent();

        Vec3d posMotion = new Vec3d(delta.x * horizontalPacket.getValue().getCurrent(), movement.y * verticalPacket.getValue().getCurrent(), delta.y * horizontalPacket.getValue().getCurrent());
        for (int i = 0; i < 3; i++) {
            getConnection().sendPacket(new CPacketPlayer.PositionRotation.Position(getPlayer().posX + posMotion.x, getPlayer().posY + posMotion.y, getPlayer().posZ + posMotion.z, getPlayer().onGround));
        }
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onPacketReceive(PacketReceiveEvent e) {
        if (getPlayer().ticksExisted < 5) return;
        if (!(e.getPacket() instanceof SPacketPlayerPosLook)) return;
        SPacketPlayerPosLook packet = (SPacketPlayerPosLook) e.getPacket();
        teleportId = packet.getTeleportId();
        getConnection().sendPacket(new CPacketConfirmTeleport(teleportId));
        if (getPlayer().getDistanceSq(packet.getX(), packet.getY(), packet.getZ()) > 4) {
            teleportId2 = teleportId;
            getConnection().sendPacket(new CPacketPlayer.Position(packet.getX(), packet.getY(), packet.getZ(), getPlayer().onGround));
            getPlayer().setPosition(packet.getX(), packet.getY(), packet.getZ());
        }
        e.setCancelled(true);
    }

    @Override
    public String getDefaultName() {
        return "PacketFly";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.MOVEMENT;
    }
}
