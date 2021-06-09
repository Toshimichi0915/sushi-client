package net.toshimichi.sushi.modules.render;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketMaps;
import net.minecraft.world.EnumSkyBlock;
import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.RootConfigurations;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.client.LightUpdateEvent;
import net.toshimichi.sushi.events.packet.PacketReceiveEvent;
import net.toshimichi.sushi.modules.*;

import java.io.IOException;

public class NoRenderModule extends BaseModule {

    private final Configuration<Boolean> skyLight;
    private final Configuration<Boolean> blockLight;
    private final Configuration<Boolean> mapIcons;

    public NoRenderModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        skyLight = provider.get("sky_light", "Sky Light", null, Boolean.class, false);
        blockLight = provider.get("block_light", "Block Light", null, Boolean.class, false);
        mapIcons = provider.get("map_icons", "Map Icons", null, Boolean.class, false);
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
        return "NoRender";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.PLAYER;
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onCheckLight(LightUpdateEvent e) {
        if ((skyLight.getValue() && e.getEnumSkyBlock() == EnumSkyBlock.SKY) ||
                (blockLight.getValue() && e.getEnumSkyBlock() == EnumSkyBlock.BLOCK))
            e.setCancelled(true);
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onPacketReceive(PacketReceiveEvent e) throws IOException {
        if (!mapIcons.getValue()) return;
        if (!(e.getPacket() instanceof SPacketMaps)) return;
        SPacketMaps packet = (SPacketMaps) e.getPacket();
        PacketBuffer read = new PacketBuffer(Unpooled.buffer());
        PacketBuffer write = new PacketBuffer(Unpooled.buffer());
        packet.writePacketData(read);
        // map-id, mapScale, trackingPosition, iconsLength, icons, columns
        write.writeVarInt(read.readVarInt());
        write.writeByte(read.readByte());
        write.writeBoolean(read.readBoolean());
        int iconsLength = read.readVarInt();
        write.writeVarInt(0);
        read.skipBytes(iconsLength * 3);
        byte columns = read.readByte();
        write.writeByte(columns);
        if (columns > 0) {
            // rows, minX, minZ, mapDataBytes
            write.writeByte(read.readByte());
            write.writeByte(read.readByte());
            write.writeByte(read.readByte());
            write.writeByteArray(read.readByteArray());
        }
        packet.readPacketData(write);
    }
}