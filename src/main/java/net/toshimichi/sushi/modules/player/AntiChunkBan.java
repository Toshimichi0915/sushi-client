package net.toshimichi.sushi.modules.player;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.toshimichi.sushi.config.Configurations;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.packet.PacketReceiveEvent;
import net.toshimichi.sushi.modules.*;

public class AntiChunkBan extends BaseModule {
    public AntiChunkBan(String id, Modules modules, Categories categories, Configurations provider, ModuleFactory factory) {
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

    @Override
    public String getDefaultName() {
        return "AntiChunkBan";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.PLAYER;
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onPacketReceive(PacketReceiveEvent e) {
        Packet<?> p = e.getPacket();
        BlockPos pos = Minecraft.getMinecraft().player.getPosition();
        if (p instanceof SPacketBlockChange) {
            SPacketBlockChange c = (SPacketBlockChange) p;
//            if (c.getBlockPosition().distanceSq(pos) > 20) {
            e.setCancelled(true);
//            }
        } else if (p instanceof SPacketUpdateTileEntity) {
            SPacketUpdateTileEntity c = (SPacketUpdateTileEntity) p;
//            if (c.getPos().distanceSq(pos)) {
            e.setCancelled(true);
//            }
        } else if (p instanceof SPacketMultiBlockChange) {
            e.setCancelled(true);
        } else if (p instanceof SPacketChunkData) {
            SPacketChunkData data = (SPacketChunkData) p;
            Chunk chunk = Minecraft.getMinecraft().world.getChunk(data.getChunkX(), data.getChunkZ());
            if (chunk.isLoaded())
                e.setCancelled(true);
        } else if(p instanceof SPacketBlockAction) {
            e.setCancelled(true);
        }
    }
}