package net.toshimichi.sushi.modules.player;

import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.SPacketCooldown;
import net.minecraft.network.play.server.SPacketEntityTeleport;
import net.minecraft.network.play.server.SPacketUnloadChunk;
import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.ConfigurationCategory;
import net.toshimichi.sushi.config.Configurations;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.packet.PacketReceiveEvent;
import net.toshimichi.sushi.events.packet.PacketSendEvent;
import net.toshimichi.sushi.modules.*;

import java.util.HashMap;
import java.util.Map;

public class PacketCancellerModule extends BaseModule {

    private final HashMap<Configuration<Boolean>, Class<?>> cancelled = new HashMap<>();

    // server bound
    private final Configuration<Boolean> playerPosition;
    private final Configuration<Boolean> playerPositionAndLook;
    private final Configuration<Boolean> playerLook;
    private final Configuration<Boolean> vehicleMove;
    private final Configuration<Boolean> entityAction;
    private final Configuration<Boolean> closeWindow;
    private final Configuration<Boolean> teleportConfirm;

    // client bound
    private final Configuration<Boolean> setCooldown;
    private final Configuration<Boolean> unloadChunk;
    private final Configuration<Boolean> entityTeleport;

    private Configuration<Boolean> newBool(ConfigurationCategory category, String id, String name, Class<?> packetClass) {
        Configuration<Boolean> conf = getConfigurations().get(id, name, null, Boolean.class, false, () -> true, category, false, 0);
        cancelled.put(conf, packetClass);
        return conf;
    }

    public PacketCancellerModule(String id, Modules modules, Categories categories, Configurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        ConfigurationCategory serverBound = provider.getCategory("server_bound", "Server Bound", "Packets sent to the server");
        ConfigurationCategory clientBound = provider.getCategory("client_bound", "Client Bound", "Packets sent to the client");
        playerPosition = newBool(serverBound, "player_position", "Player Position", CPacketPlayer.Position.class);
        playerPositionAndLook = newBool(serverBound, "player_position_and_look", "Player Position And Look", CPacketPlayer.PositionRotation.class);
        playerLook = newBool(serverBound, "player_look", "Player Look", CPacketPlayer.Rotation.class);
        vehicleMove = newBool(serverBound, "vehicle_move", "Vehicle Move", CPacketVehicleMove.class);
        entityAction = newBool(serverBound, "entity_action", "Entity Action", CPacketEntityAction.class);
        closeWindow = newBool(serverBound, "close_window", "Close Window", CPacketCloseWindow.class);
        teleportConfirm = newBool(serverBound, "teleport_confirm", "Teleport Confirm", CPacketConfirmTeleport.class);

        setCooldown = newBool(clientBound, "set_cooldown", "Set Cooldown", SPacketCooldown.class);
        unloadChunk = newBool(clientBound, "unload_chunk", "Unload Chunk", SPacketUnloadChunk.class);
        entityTeleport = newBool(clientBound, "entity_teleport", "Entity Teleport", SPacketEntityTeleport.class);
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
    }

    private boolean check(Object packet) {
        for (Map.Entry<Configuration<Boolean>, Class<?>> entry : cancelled.entrySet()) {
            if (entry.getKey().getValue() && packet.getClass().equals(entry.getValue()))
                return true;
        }
        return false;
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onPacketSend(PacketSendEvent e) {
        e.setCancelled(check(e.getPacket()));
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onPacketReceive(PacketReceiveEvent e) {
        e.setCancelled(check(e.getPacket()));
    }

    @Override
    public String getDefaultName() {
        return "PacketCanceller";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.PLAYER;
    }
}
