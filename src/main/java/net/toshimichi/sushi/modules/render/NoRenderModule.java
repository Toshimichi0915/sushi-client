package net.toshimichi.sushi.modules.render;

import io.netty.buffer.Unpooled;
import net.minecraft.client.tutorial.TutorialSteps;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.init.MobEffects;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketMaps;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.ConfigurationCategory;
import net.toshimichi.sushi.config.RootConfigurations;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.client.LightUpdateEvent;
import net.toshimichi.sushi.events.packet.PacketReceiveEvent;
import net.toshimichi.sushi.events.render.BlockOverlayRenderEvent;
import net.toshimichi.sushi.events.render.EntityRenderEvent;
import net.toshimichi.sushi.events.render.GameOverlayRenderEvent;
import net.toshimichi.sushi.events.render.HurtCameraEffectEvent;
import net.toshimichi.sushi.events.tick.ClientTickEvent;
import net.toshimichi.sushi.modules.*;

import java.io.IOException;

public class NoRenderModule extends BaseModule {

    private final Configuration<Boolean> skyLight;
    private final Configuration<Boolean> blockLight;
    private final Configuration<Boolean> mapIcons;

    private final Configuration<Boolean> hurtCam;
    private final Configuration<Boolean> portal;
    private final Configuration<Boolean> tutorial;
    private final Configuration<Boolean> potionIcons;
    private final Configuration<Boolean> vignette;
    private final Configuration<Boolean> blindness;
    private final Configuration<Boolean> nausea;
    private final Configuration<Boolean> pumpkin;
    private final Configuration<Boolean> block;
    private final Configuration<Boolean> fire;
    private final Configuration<Boolean> water;

    private final Configuration<Boolean> explosion;
    private final Configuration<Boolean> firework;

    public NoRenderModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        ConfigurationCategory chunkBan = provider.getCategory("chunk_ban", "Chunk BAN", null);
        skyLight = chunkBan.get("sky_light", "Sky Light", null, Boolean.class, false);
        blockLight = chunkBan.get("block_light", "Block Light", null, Boolean.class, false);
        mapIcons = chunkBan.get("map_icons", "Map Icons", null, Boolean.class, false);

        ConfigurationCategory overlay = provider.getCategory("overlay", "Overlay", null);
        hurtCam = overlay.get("hurt_cam", "Hurt Cam", null, Boolean.class, false);
        portal = overlay.get("portal", "Portal", null, Boolean.class, false);
        tutorial = overlay.get("tutorial", "Tutorial", null, Boolean.class, false);
        potionIcons = overlay.get("potion_icons", "Potion Icons", null, Boolean.class, false);
        vignette = overlay.get("vignette", "Vignette", null, Boolean.class, false);
        blindness = overlay.get("blindness", "Blindness", null, Boolean.class, false);
        nausea = overlay.get("nausea", "Nausea", null, Boolean.class, false);
        pumpkin = overlay.get("pumpkin", "Pumpkin", null, Boolean.class, false);
        block = overlay.get("block", "Block", null, Boolean.class, false);
        fire = overlay.get("fire", "Fire", null, Boolean.class, false);
        water = overlay.get("water", "Water", null, Boolean.class, false);

        ConfigurationCategory world = provider.getCategory("world", "World", null);
        explosion = world.get("explosion", "Explosion", null, Boolean.class, false);
        firework = world.get("firework", "Firework", null, Boolean.class, false);
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
    public void onClientTick(ClientTickEvent e) {
        if (blindness.getValue()) getPlayer().removeActivePotionEffect(MobEffects.BLINDNESS);
        if (nausea.getValue()) getPlayer().removeActivePotionEffect(MobEffects.NAUSEA);
        if (tutorial.getValue()) getClient().gameSettings.tutorialStep = TutorialSteps.NONE;
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onGameOverlayRender(GameOverlayRenderEvent e) {
        if (potionIcons.getValue() && e.getType() == RenderGameOverlayEvent.ElementType.POTION_ICONS ||
                portal.getValue() && e.getType() == RenderGameOverlayEvent.ElementType.PORTAL ||
                vignette.getValue() && e.getType() == RenderGameOverlayEvent.ElementType.VIGNETTE ||
                pumpkin.getValue() && e.getType() == RenderGameOverlayEvent.ElementType.HELMET) {

            e.setCancelled(true);
        }
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onHurtCameraEffect(HurtCameraEffectEvent e) {
        if (hurtCam.getValue()) e.setCancelled(true);
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onBlockOverlayRender(BlockOverlayRenderEvent e) {
        if (block.getValue() && e.getOverlayType() == RenderBlockOverlayEvent.OverlayType.BLOCK ||
                fire.getValue() && e.getOverlayType() == RenderBlockOverlayEvent.OverlayType.FIRE ||
                water.getValue() && e.getOverlayType() == RenderBlockOverlayEvent.OverlayType.WATER) {
            e.setCancelled(true);
        }
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onEntityRender(EntityRenderEvent e) {
        if (firework.getValue() && e.getEntityIn() instanceof EntityFireworkRocket) {
            e.setCancelled(true);
        }
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onCheckLight(LightUpdateEvent e) {
        if ((skyLight.getValue() && e.getEnumSkyBlock() == EnumSkyBlock.SKY) ||
                (blockLight.getValue() && e.getEnumSkyBlock() == EnumSkyBlock.BLOCK))
            e.setCancelled(true);
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onPacketReceive(PacketReceiveEvent e) throws IOException {
        if (e.getPacket() instanceof SPacketMaps) {
            if (!mapIcons.getValue()) return;
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
        } else if (e.getPacket() instanceof SPacketExplosion) {
            if (explosion.getValue()) e.setCancelled(true);
        } else if (e.getPacket() instanceof SPacketSpawnObject) {
            SPacketSpawnObject packet = (SPacketSpawnObject) e.getPacket();
            if (packet.getType() == 76) e.setCancelled(true);
        }
    }
}